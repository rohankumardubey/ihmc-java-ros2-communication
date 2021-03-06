/*
 * Copyright 2017 Florida Institute for Human and Machine Cognition (IHMC)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.ihmc.ros2.rosidl;

import us.ihmc.commons.exception.DefaultExceptionHandler;
import us.ihmc.commons.nio.BasicPathVisitor;
import us.ihmc.commons.nio.FileTools;
import us.ihmc.commons.nio.PathTools;
import us.ihmc.commons.nio.WriteOption;
import us.ihmc.idl.generator.IDLGenerator;
import us.ihmc.rosidl.Ros2MsgToIdlGenerator;
import us.ihmc.rosidl.Ros2MsgToRos1MsgGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Utility to convert ROS2 IDL files (.msg & .srv) to Java files compatible with IHMC Pub/Sub
 *
 * @author Jesper Smith
 */
public class RosInterfaceGenerator
{
   private final Ros2MsgToRos1MsgGenerator ros2MsgToRos1MsgGenerator;
   private Ros2MsgToIdlGenerator ros2MsgToIdlGenerator;

   // Holder for all packages found
   private final HashMap<String, Path> customIDLFiles = new HashMap<>();

   /**
    * Compile Ros interfaces to Java files for IHMC pub sub
    *
    * @throws IOException if no temporary files and directories can be made
    */
   public RosInterfaceGenerator() throws IOException
   {
      ros2MsgToIdlGenerator = new Ros2MsgToIdlGenerator();
      ros2MsgToRos1MsgGenerator = new Ros2MsgToRos1MsgGenerator();
   }

   /**
    * Add a directory with ros packages to the list of interfaces to be compiled.
    *
    * The expected directory structure is
    * - rootPath
    * - packageName
    * - package.xml
    *
    * A package xml with at least <name /> and optionally <build_depends />
    *
    * @param rootPath The root directory of packages to add
    * @throws IOException If the rootPath cannot be read
    */
   public void addPackageRootToIDLGenerator(Path rootPath) throws IOException
   {
      ros2MsgToIdlGenerator.addPackageRoot(rootPath);
   }

   /**
    * Add a directory with ros packages to the list of interfaces to be compiled.
    *
    * The expected directory structure is
    * - rootPath
    * - packageName
    * - package.xml
    *
    * A package xml with at least <name /> and optionally <build_depends />
    *
    * @param rootPath The root directory of packages to add
    * @throws IOException If the rootPath cannot be read
    */
   public void addPackageRootToROS1Generator(Path rootPath) throws IOException
   {
      ros2MsgToRos1MsgGenerator.addPackageRoot(rootPath);
   }

   /**
    * Generate java files for all packages.
    *
    * This function can ber called
    *
    * @param idlDirectory directory to put .idl files in
    * @param javaDirectory directory to put generated .java files in
    * @throws IOException
    */
   public void generate(Path idlDirectory, Path ros1Directory, Path javaDirectory) throws IOException
   {
      // Convert all packages to .idl
      ros2MsgToIdlGenerator.convertToIDL(idlDirectory);
      ros2MsgToRos1MsgGenerator.convertToROS1(ros1Directory);

      // Copy custom idl files to the idl directory
      customIDLFiles.forEach((key, path) -> {
         try
         {
            FileTools.ensureDirectoryExists(idlDirectory.resolve(key).getParent());
            Files.copy(path, idlDirectory.resolve(key), StandardCopyOption.REPLACE_EXISTING);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      });

      //IDLGenerator.execute(idlFile, packageName, targetDirectory, includePath);
      Files.find(idlDirectory, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.getFileName().toString().endsWith(".idl"))
           .forEach((file) -> generateJava(file, javaDirectory, idlDirectory));
   }

   /**
    * Add a directory with custom .idl files. These overload the .idl files generated by the ROS2 IDL generator
    *
    * @param rootPath root path of idl files
    * @throws IOException If the root path cannot be read
    */
   public void addCustomIDLFiles(Path rootPath) throws IOException
   {
      Files.find(rootPath, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.getFileName().toString().endsWith(".idl"))
           .forEach((file) -> addCustomIDLFile(file, rootPath));
   }

   /**
    * Helper function to add custom IDL files
    *
    * @param path
    * @param rootPath
    */
   private void addCustomIDLFile(Path path, Path rootPath)
   {
      String key = rootPath.relativize(path).toString();
      customIDLFiles.put(key, path);
   }

   /**
    * Helper function to convert a single .idl file in a java file
    *
    * This function will check if there is a custom verison of the .idl file registered.
    *
    * @param idlFile
    * @param javaDirectory
    * @param idlIncludeDirectory
    */
   private void generateJava(Path idlFile, Path javaDirectory, Path idlIncludeDirectory)
   {
      //      System.out.println("[IDL -> PubSub] Parsing " + idlFile);

      try
      {
         IDLGenerator.execute(idlFile.toFile(), "", javaDirectory.toFile(), Collections.singletonList(idlIncludeDirectory.toFile()));
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Convert a directory to Unix EOL.
    *
    * @param directory
    */
   public static void convertDirectoryToUnixEOL(Path directory)
   {
      PathTools.walkRecursively(directory, new BasicPathVisitor()
      {
         @Override
         public FileVisitResult visitPath(Path path, PathType pathType)
         {
            if (pathType == PathType.FILE)
            {
               List<String> lines = FileTools.readAllLines(path, DefaultExceptionHandler.PRINT_STACKTRACE);

               PrintWriter printer = FileTools.newPrintWriter(path, WriteOption.TRUNCATE, DefaultExceptionHandler.PRINT_STACKTRACE);
               lines.forEach(line -> printer.print(line + "\n"));
               printer.close();
            }

            return FileVisitResult.CONTINUE;
         }
      });
   }

   /**
    * Basically, in order to generate messages based on other packages, you must regenerate them as well.
    *
    * This will probably be fixed in a future version.
    *
    * @param outputDirectory
    * @param packagesToKeep
    */
   public static void deleteAllExcept(Path outputDirectory, String... packagesToKeep)
   {
      PathTools.walkFlat(outputDirectory, new BasicPathVisitor()
      {
         @Override
         public FileVisitResult visitPath(Path path, PathType pathType)
         {
            if (!keep(path.getFileName().toString(), packagesToKeep))
            {
               FileTools.deleteQuietly(path);
            }

            return FileVisitResult.CONTINUE;
         }
      });
   }

   private static boolean keep(String candidate, String... packagesToKeep)
   {
      for (String str : packagesToKeep)
         if (candidate.equals(str))
            return true;

      return false;
   }
}

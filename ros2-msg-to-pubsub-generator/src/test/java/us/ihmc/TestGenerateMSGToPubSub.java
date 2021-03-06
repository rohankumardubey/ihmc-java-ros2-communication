package us.ihmc;

import us.ihmc.commons.nio.FileTools;
import us.ihmc.ros2.rosidl.RosInterfaceGenerator;

import java.io.IOException;
import java.nio.file.Paths;

public class TestGenerateMSGToPubSub
{
   public static void main(String[] args) throws IOException
   {
      FileTools.deleteQuietly(Paths.get("generated-java"));
      FileTools.deleteQuietly(Paths.get("generated-idl"));

      RosInterfaceGenerator generator = new RosInterfaceGenerator();

      generator.addPackageRootToIDLGenerator(Paths.get("ros_msgs"));
      generator.addPackageRootToROS1Generator(Paths.get("ros_msgs"));

      generator.addCustomIDLFiles(Paths.get("custom-idl"));

      generator.generate(Paths.get("generated-idl"), Paths.get("generated-ros1"), Paths.get("generated-java"));

      RosInterfaceGenerator.convertDirectoryToUnixEOL(Paths.get("generated-idl"));
      RosInterfaceGenerator.convertDirectoryToUnixEOL(Paths.get("generated-ros1"));
      RosInterfaceGenerator.convertDirectoryToUnixEOL(Paths.get("generated-java"));
   }
}

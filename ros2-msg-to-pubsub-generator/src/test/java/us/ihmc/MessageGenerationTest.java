package us.ihmc;

import org.junit.jupiter.api.Test;
import us.ihmc.log.LogTools;
import us.ihmc.commons.nio.FileTools;
import us.ihmc.ros2.rosidl.RosInterfaceGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageGenerationTest
{
   @Test// timeout = 30000
   public void testMessageGeneration() throws IOException
   {
      Path startingPath;
      LogTools.info("Working dir: " + Paths.get(".").toAbsolutePath());
      startingPath = Paths.get(".");

      FileTools.deleteQuietly(startingPath.resolve("generated-java"));
      FileTools.deleteQuietly(startingPath.resolve("generated-idl"));

      RosInterfaceGenerator generator = new RosInterfaceGenerator();
      generator.addPackageRootToIDLGenerator(startingPath.resolve("ros_msgs"));
      generator.addPackageRootToROS1Generator(startingPath.resolve("ros_msgs"));
      generator.addCustomIDLFiles(startingPath.resolve("custom-idl"));
      generator.generate(startingPath.resolve("generated-idl"), Paths.get("generated-ros1"), startingPath.resolve("generated-java"));
   }
}

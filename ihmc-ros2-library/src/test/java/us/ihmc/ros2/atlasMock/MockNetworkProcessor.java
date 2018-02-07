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
package us.ihmc.ros2.atlasMock;

import controller_msgs.msg.dds.RobotConfigurationData;
import controller_msgs.msg.dds.RobotConfigurationDataPubSubType;
import us.ihmc.pubsub.common.MatchingInfo;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.pubsub.subscriber.SubscriberListener;
import us.ihmc.ros2.IntraProcessNode;
import us.ihmc.ros2.RosNode;

import java.io.IOException;

public class MockNetworkProcessor
{
   public static void subscribeUsingRosNode() throws IOException, InterruptedException
   {

      RosNode node = new RosNode("MockNetworkProcessor");
      //      node.createSubscription(new AtlasRobotConfigurationDataPubSubType(), new Callback(), "/robot_configuration_data");
      node.createSubscription(new RobotConfigurationDataPubSubType(), new Callback(), "/robot_configuration_data");
      Thread.currentThread().join();
   }

   public static class Callback implements SubscriberListener
   {
      //      AtlasRobotConfigurationData robotConfigurationData = new AtlasRobotConfigurationData();
      RobotConfigurationData robotConfigurationData = new RobotConfigurationData();

      @Override
      public void onNewDataMessage(Subscriber subscriber)
      {
         try
         {
            if (subscriber.takeNextData(robotConfigurationData, null))
            {
               //               RobotConfigurationData data = ...
               //               System.out.println(robotConfigurationData.getRobot_configuration_data().getTimestamp());
               //               System.out.println(robotConfigurationData.getTimestamp());
               System.out.println(robotConfigurationData.getHeader().getStamp().getNanosec());
            }
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }

      @Override
      public void onSubscriptionMatched(Subscriber subscriber, MatchingInfo info)
      {
         System.out.println("Subscription matched!: " + subscriber.getAttributes().getTopic().getTopicName() + " " + info.getStatus().name());
      }
   }

   public static void main(String[] args) throws IOException, InterruptedException
   {
      subscribeUsingRosNode();
   }
}

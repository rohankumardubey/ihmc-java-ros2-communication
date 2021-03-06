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
package us.ihmc.ros2;

import java.io.IOException;

import us.ihmc.pubsub.Domain;
import us.ihmc.pubsub.publisher.Publisher;

/**
 * Simple ROS2 publisher
 * 
 * 
 * @author Jesper Smith
 *
 * @param <T> The data type to send
 */
public class Ros2Publisher<T>
{
   private final Domain domain;
   private final Publisher publisher;

   Ros2Publisher(Domain domain, Publisher publisher)
   {
      this.domain = domain;
      this.publisher = publisher;
   }

   /**
    * Publish data to the ROS2 domain
    * 
    * @param data Data to publisher
    * @throws IOException If the data cannot be serialized or another error occurs
    */
   public void publish(T data) throws IOException
   {
      publisher.write(data);
   }

   /**
    * Remove this publisher from the ROS domain
    */
   public void remove()
   {
      domain.removePublisher(publisher);
   }
}

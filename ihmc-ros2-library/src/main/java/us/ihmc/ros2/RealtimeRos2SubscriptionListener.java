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

import us.ihmc.concurrent.ConcurrentRingBuffer;
import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.common.MatchingInfo;
import us.ihmc.pubsub.subscriber.Subscriber;

/**
 * Helper listener for the Realtime subscription.
 * 
 * This class moves all incoming messages in the queue if there is space available.
 * 
 * @author Jesper Smith
 *
 * @param <T>
 */
class RealtimeRos2SubscriptionListener<T> implements NewMessageListener<T>
{
   private final T data;
   private final ConcurrentRingBuffer<T> messageQueue;
   private final TopicDataType<T> topicDataTypeForCallback;
   private final TopicDataType<T> topicDataTypeForPoll;

   RealtimeRos2SubscriptionListener(TopicDataType<T> topicDataType, int queueDepth)
   {
      this.data = topicDataType.createData();
      this.messageQueue = new ConcurrentRingBuffer<>(() -> topicDataType.createData(), queueDepth);
      this.topicDataTypeForCallback = topicDataType.newInstance();
      this.topicDataTypeForPoll = topicDataType.newInstance();
   }

   
   boolean poll(T data)
   {
     if(messageQueue.poll()) 
     {
        T next = messageQueue.read();
        topicDataTypeForPoll.copy(next, data);
        messageQueue.flush();
        return true;
     }
     else
     {
        return false;
     }
   }
   

   boolean flushAndGetLatest(T data)
   {
      if(messageQueue.poll())
      {
         T latest = null;
         T next = null;
         while((next = messageQueue.read()) != null)
         {
            latest = next;
         }
         topicDataTypeForPoll.copy(latest, data);
         messageQueue.flush();
         return true;
      }
      else
      {
         return false;
      }
   }
   
   @Override
   public void onNewDataMessage(Subscriber<T> subscriber)
   {
      if (subscriber.takeNextData(data, null))
      {
         T next = messageQueue.next();
         if (next != null)
         {
            topicDataTypeForCallback.copy(data, next);
            messageQueue.commit();
         }
      }
   }

   @Override
   public void onSubscriptionMatched(Subscriber<T> subscriber, MatchingInfo info)
   {

   }


}
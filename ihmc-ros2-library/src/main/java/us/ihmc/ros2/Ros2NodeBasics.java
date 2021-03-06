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

import us.ihmc.commons.PrintTools;
import us.ihmc.pubsub.Domain;
import us.ihmc.pubsub.DomainFactory;
import us.ihmc.pubsub.DomainFactory.PubSubImplementation;
import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.attributes.DurabilityKind;
import us.ihmc.pubsub.attributes.ParticipantAttributes;
import us.ihmc.pubsub.attributes.PublishModeKind;
import us.ihmc.pubsub.attributes.PublisherAttributes;
import us.ihmc.pubsub.attributes.SubscriberAttributes;
import us.ihmc.pubsub.attributes.TopicAttributes.TopicKind;
import us.ihmc.pubsub.common.MatchingInfo;
import us.ihmc.pubsub.participant.Participant;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.pubsub.subscriber.SubscriberListener;

/**
 * Internal class to share implementation between intra and inter process ROS2 nodes.
 *
 * @author Jesper Smith
 * @author Duncan Calvert
 *
 */
class Ros2NodeBasics
{
   public static final int ROS_DEFAULT_DOMAIN_ID = domainFromEnvironment();

   private final Ros2Distro ros2Distro;
   
   private Domain domain;
   private Participant participant;

   private final String nodeName;
   private final String namespace;

   /**
    * Create a new ROS2 node.
    *
    *
    * @param name Name for the node
    * @param namespace namespace for the ros node i.e. DDS partition
    * @param domainId Domain ID for the ros node
    * @throws IOException if no participant can be made
    */
   Ros2NodeBasics(PubSubImplementation pubSubImplementation, Ros2Distro ros2Distro, String name, String namespace, int domainId) throws IOException
   {
      this.domain = DomainFactory.getDomain(pubSubImplementation);
      this.ros2Distro = ros2Distro;
      
      Ros2TopicNameMangler.checkNodename(name);
      Ros2TopicNameMangler.checkNamespace(namespace);

      this.nodeName = name;
      this.namespace = namespace;

      ParticipantAttributes attr = domain.createParticipantAttributes(domainId, name);
      participant = domain.createParticipant(attr);
   }

   /**
    * Create a new ROS2 compatible publisher in this Node
    *
    * This call makes a publisher with the default settings
    *
    * @param topicDataType The topic data type of the message
    * @param topicName Name for the topic
    * @return A ROS publisher
    *
    * @throws IOException if no publisher can be made
    */
   public <T> Ros2Publisher<T> createPublisher(TopicDataType<T> topicDataType, String topicName) throws IOException
   {
      return createPublisher(topicDataType, topicName, Ros2QosProfile.DEFAULT());
   }

   /**
    * Create a new ROS2 compatible publisher in this Node
    *
    * @param topicDataType The topic data type of the message
    * @param topicName Name for the topic
    * @param qosProfile ROS Qos Profile
    * @return A ROS publisher
    *
    * @throws IOException if no publisher can be made
    */
   public <T> Ros2Publisher<T> createPublisher(TopicDataType<T> topicDataType, String topicName, Ros2QosProfile qosProfile) throws IOException
   {
      TopicDataType<?> registeredType = domain.getRegisteredType(participant, topicDataType.getName());
      if (registeredType == null)
      {
         domain.registerType(participant, topicDataType);
      }

      PublisherAttributes publisherAttributes = domain.createPublisherAttributes();
      publisherAttributes.getTopic().setTopicKind(topicDataType.isGetKeyDefined() ? TopicKind.WITH_KEY : TopicKind.NO_KEY);
      publisherAttributes.getTopic().setTopicDataType(topicDataType.getName());

      publisherAttributes.getQos().setReliabilityKind(qosProfile.getReliability());

      switch (qosProfile.getDurability())
      {
      case TRANSIENT_LOCAL:
         publisherAttributes.getQos().setDurabilityKind(DurabilityKind.TRANSIENT_LOCAL_DURABILITY_QOS);
         break;
      case VOLATILE:
         publisherAttributes.getQos().setDurabilityKind(DurabilityKind.VOLATILE_DURABILITY_QOS);
         break;
      }

      publisherAttributes.getTopic().getHistoryQos().setDepth(qosProfile.getSize());
      publisherAttributes.getTopic().getHistoryQos().setKind(qosProfile.getHistory());

      Ros2TopicNameMangler.assignNameAndPartitionsToAttributes(ros2Distro, publisherAttributes, namespace, nodeName, topicName, qosProfile.isAvoidRosNamespaceConventions());

      if (topicDataType.getTypeSize() > 65000)
      {
         publisherAttributes.getQos().setPublishMode(PublishModeKind.ASYNCHRONOUS_PUBLISH_MODE);
      }

      return new Ros2Publisher<>(domain, domain.createPublisher(participant, publisherAttributes));

   }

   /**
    * Create a new ROS2 compatible subscription.
    *
    * This call can be used to make a ROS2 topic with the default qos profile
    *
    *
    * @param topicDataType The topic data type of the message
    * @param newMessageListener New message listener
    * @param topicName Name for the topic
    * @return Ros Subscription
    * @throws IOException if no subscriber can be made
    */
   public <T> Ros2Subscription<T> createSubscription(TopicDataType<T> topicDataType, NewMessageListener<T> newMessageListener, String topicName) throws IOException
   {
      return createSubscription(topicDataType, newMessageListener, topicName, Ros2QosProfile.DEFAULT());
   }

   /**
    * Create a new ROS2 compatible subscription.
    *
    * This call can be used to make a ROS2 topic with the default qos profile
    *
    * @param topicDataType The topic data type of the message
    * @param newMessageListener New message listener
    * @param topicName Name for the topic
    * @return Ros Subscription
    * @throws IOException if no subscriber can be made
    */
   public <T> Ros2Subscription<T> createSubscription(TopicDataType<T> topicDataType, NewMessageListener<T> newMessageListener, String topicName,
                                                     Ros2QosProfile qosProfile) throws IOException
   {
      return createSubscription(topicDataType, (SubscriberListener<T>) newMessageListener, topicName, qosProfile);
   }

   /**
    * Create a new ROS2 compatible subscription.
    *
    * This call can be used to make a ROS2 topic with the default qos profile
    *
    * @param topicDataType The topic data type of the message
    * @param newMessageListener New message listener
    * @param subscriptionMatchedListener Subscription matched listener
    * @param topicName Name for the topic
    * @param qosProfile ROS Qos Profile
    * @return Ros Subscription
    * @throws IOException if no subscriber can be made
    */
   public <T> Ros2Subscription<T> createSubscription(TopicDataType<T> topicDataType, NewMessageListener<T> newMessageListener,
                                                     SubscriptionMatchedListener<T> subscriptionMatchedListener, String topicName,
                                                     Ros2QosProfile qosProfile) throws IOException
   {

      return createSubscription(topicDataType, new SubscriberListener<T>()
      {
         @Override
         public void onNewDataMessage(Subscriber<T> subscriber)
         {
            newMessageListener.onNewDataMessage(subscriber);
         }

         @Override
         public void onSubscriptionMatched(Subscriber<T> subscriber, MatchingInfo info)
         {
            subscriptionMatchedListener.onSubscriptionMatched(subscriber, info);
         }
      }, topicName, qosProfile);
   }

   /**
    * Create a new ROS2 compatible subscription.
    *
    * @param topicDataType The topic data type of the message
    * @param topicName Name for the topic
    * @param qosProfile ROS Qos Profile
    * @return Ros Subscription
    * @throws IOException if no subscriber can be made
    */
   private <T> Ros2Subscription<T> createSubscription(TopicDataType<T> topicDataType, SubscriberListener<T> subscriberListener, String topicName,
                                                      Ros2QosProfile qosProfile) throws IOException
   {
      TopicDataType<?> registeredType = domain.getRegisteredType(participant, topicDataType.getName());
      if (registeredType == null)
      {
         domain.registerType(participant, topicDataType);
      }

      SubscriberAttributes subscriberAttributes = domain.createSubscriberAttributes();
      subscriberAttributes.getTopic().setTopicKind(topicDataType.isGetKeyDefined() ? TopicKind.WITH_KEY : TopicKind.NO_KEY);
      subscriberAttributes.getTopic().setTopicDataType(topicDataType.getName());
      subscriberAttributes.getTopic().setTopicName(topicName);
      subscriberAttributes.getQos().setReliabilityKind(qosProfile.getReliability());

      switch (qosProfile.getDurability())
      {
      case TRANSIENT_LOCAL:
         subscriberAttributes.getQos().setDurabilityKind(DurabilityKind.TRANSIENT_LOCAL_DURABILITY_QOS);
         break;
      case VOLATILE:
         subscriberAttributes.getQos().setDurabilityKind(DurabilityKind.VOLATILE_DURABILITY_QOS);
         break;
      }

      subscriberAttributes.getTopic().getHistoryQos().setDepth(qosProfile.getSize());
      subscriberAttributes.getTopic().getHistoryQos().setKind(qosProfile.getHistory());

      Ros2TopicNameMangler
            .assignNameAndPartitionsToAttributes(ros2Distro, subscriberAttributes, namespace, nodeName, topicName, qosProfile.isAvoidRosNamespaceConventions());

      return new Ros2Subscription<>(domain, domain.createSubscriber(participant, subscriberAttributes, subscriberListener));
   }

   /**
    * 
    * @return the name of this node
    */
   public String getName()
   {
      return nodeName;
   }
   
   /**
    * 
    * @return the namespace of this node
    */
   public String getNamespace()
   {
      return namespace;
   }
   
   /**
    * Destroys this node.
    * <p>
    * This effectively removes this node's {@code Participant} from the domain and clear the
    * internal references to these two.
    * </p>
    * <p>
    * After calling this method, this node becomes unusable, i.e. publisher or subscriber can no
    * longer be created.
    * </p>
    */
   public void destroy()
   {
      domain.removeParticipant(participant);
      domain = null;
      participant = null;
   }

   public static int domainFromEnvironment()
   {
      String rosDomainId = System.getenv("ROS_DOMAIN_ID");

      int rosDomainIdAsInteger = 0; // default to 0

      if (rosDomainId != null)
      {
         rosDomainId = rosDomainId.trim();
         try
         {
            rosDomainIdAsInteger = Integer.valueOf(rosDomainId);
         }
         catch (NumberFormatException e)
         {
            PrintTools.warn("Environment variable ROS_DOMAIN_ID cannot be parsed as an integer: " + rosDomainId);
         }
      }

      PrintTools.info(Ros2NodeBasics.class, "Default ROS_DOMAIN_ID: " + rosDomainIdAsInteger);

      return rosDomainIdAsInteger;
   }
}

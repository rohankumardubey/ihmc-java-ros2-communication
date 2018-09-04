# IHMC Java ROS 2 Communication


ROS2 messaging for Java.

## Introduction

This library builds on [IHMC Pub Sub Group](https://github.com/ihmcrobotics/ihmc-pub-sub-group), an allocation free Java library for DDSI-RTPS messaging. It uses modified versions of [rosidl utilities](https://github.com/ros2/rosidl) to convert .msg files into Java types.

## Features

- Easy-to-use API for publishing and subscribing to ROS 2 topics
- Allocation free modules for realtime support
- Carries documentation from .msg files into Javadoc
- Gradle task for .msg -> .java generation
- Provided Java library for ROS [common_interfaces](https://github.com/ros2/common_interfaces), [rcl_interfaces](https://github.com/ros2/rcl_interfaces), and [geometry2](https://github.com/ros2/geometry2)
- ROS 2 .msg to ROS 1 .msg generation
- ROS 2 ardent and bouncy compatibility

## Download

In your build.gradle:

`compile group: "us.ihmc", name: "ihmc-ros2-library", version: `
[ ![ihmc-ros2-library](https://api.bintray.com/packages/ihmcrobotics/maven-release/ihmc-ros2-library/images/download.svg) ](https://bintray.com/ihmcrobotics/maven-release/ihmc-ros2-library/_latestVersion)
`  // publish/subscribe API`
 
`compile group: "us.ihmc", name: "ros2-common-interfaces", version: `
[ ![ros2-common-interfaces](https://api.bintray.com/packages/ihmcrobotics/maven-release/ros2-common-interfaces/images/download.svg) ](https://bintray.com/ihmcrobotics/maven-release/ros2-common-interfaces/_latestVersion)
` // ROS2 common message library`

`compile group: "us.ihmc", name: "ros2-msg-to-pubsub-generator", version: `
[ ![ros2-msg-to-pubsub-generator](https://api.bintray.com/packages/ihmcrobotics/maven-release/ros2-msg-to-pubsub-generator/images/download.svg) ](https://bintray.com/ihmcrobotics/maven-release/ros2-msg-to-pubsub-generator/_latestVersion)
` // generator for .msg -> .java`

## IHMC ROS2 Library

This library provides a minimal implementation of a Ros2Node in Java. Two versions are available:

- **Ros2Node**: Publishes in the same thread and uses direct callbacks for incoming messages.
- **RealtimeRos2Node**:	Stores outgoing and incoming messages in a queue and uses non-blocking calls to publish messages and allows polling for new messages.

### Example

###### Publish/Subscribe

```java
PeriodicThreadSchedulerFactory threadFactory = SystemUtils.IS_OS_LINUX ? // realtime threads only work on linux
      new PeriodicRealtimeThreadSchedulerFactory(20) :           // see https://github.com/ihmcrobotics/ihmc-realtime
      new PeriodicNonRealtimeThreadSchedulerFactory();                   // to setup realtime threads
RealtimeRos2Node node = new RealtimeRos2Node(PubSubImplementation.FAST_RTPS, threadFactory, "NonRealtimeRos2PublishSubscribeExample", "/us/ihmc");
RealtimeRos2Publisher<Int64> publisher = node.createPublisher(new Int64PubSubType(), "/example", Ros2QosProfile.KEEP_HISTORY(3), 10);
RealtimeRos2Subscription<Int64> subscription = node.createQueuedSubscription(new Int64PubSubType(), "/example", Ros2QosProfile.KEEP_HISTORY(3), 10);


node.spin(); // start the realtime node thread

Int64 message = new Int64();
for (int i = 0; i < 10; i++)
{
   message.setData(i);
   publisher.publish(message);
   System.out.println("Sending: " + message);
}

Int64 incomingMessage = new Int64();
while (!subscription.poll(incomingMessage))
   ; // just waiting for the first message
System.out.println("Receiving: " + incomingMessage); // first message
int i = 1;
while (i < 10)
{
   if (subscription.poll(incomingMessage))
   {
      System.out.println("Receiving: " + incomingMessage);
      i++;
   }
   else
   {
      // no available messages
   }
}
System.out.println("Received all messages!");

node.destroy();
```

## Note

The intermediate .idl files generated by this library are not valid to be used outside IHMC Pub/Sub. 

## License

Apache 2.0

## Maintainer Notes

See [docs/Making a release.md](docs/Making%20a%20release.md)
 
This project is an example of how you might upgrade from ActiveMQ to Artemis in a rolling fashion when you are using Virtual 
topics.

### Migrating Openwire consumers to Artemis
Create the activemq instance using the command 

```
mvn clean package -Pcreateactivemq
```

Now start activemq
```
./activemq-virtual-topic/target/activemq/bin/activemq start
```

Now create the artemis instance

```
mvn clean package -Pcreateartemis
```

Now run the artemis instance
```
./artemis-virtual-topic/target/artemis-instance//bin/artemis run
```

Now we can start 2 openwire consumers that will be connected to the virtual topic _VirtualTopic.TopicA_ via the queue 
_Consumer.myConsumer1.VirtualTopic.TopicA_ by running this command in 2 shells
```
mvn verify -Pactivemqowconsumer
```

Now we can start an openwire consumer connected to artemis which will connect to address _VirtualTopic.TopicA_ and create 
a multicast queue _Consumer.myConsumer1.VirtualTopic.TopicA_
```
mvn verify -Partemisowconsumer
```

now we can start sending messages to the virtual topic _VirtualTopic.TopicA_ which we will see consumed by the 2 activemq consumers
```
mvn verify -Pactivemqproducer
```

Start a camel bridge by running this command in the _virtual-topic-bridge_ directory

```
mvn exec:java -PCamelServer
```

you should now see the message also being sent to the 3rd consumer sharing the load on Artemis, you can now stop the activemq consumers
and start more if needed on Artemis. The consumers are now migrated to Artemis with no loss of service.

##Migrating consumers to use JMS 2.0 via AMQP


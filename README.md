# Partition assignment example

See the behavior of the partition assignment in different configuration

## Pre-requisites
* docker-compose
* [Confluent CLI](https://docs.confluent.io/platform/current/installation/installing_cp/zip-tar.html)

## How to run

### 1. Producer

In one terminal, start the cluster and producer (you will be asked some questions):

     ./launch-producer.sh

> For each tipic, we will feed it with 600000 records, so it can take some time :-) 

Example:

      ./launch-producer.sh
      [+] Running 3/3
      ⠿ Network kafka-partition-assignment-examples_default  Created                <-- cluster being started                                                                                                                      0.0s
      ⠿ Container zookeeper                                  Started                                                                                                                                      0.4s
      ⠿ Container broker                                     Started                                                                                                                                      0.7s
      
      Should I create a new topic? [y/n]: y
      How many partitions? 1
      Created topic topic-0.
      
      Should I create a new topic? [y/n]: y
      How many partitions? 3
      Created topic topic-1.
      
      Should I create a new topic? [y/n]: y
      How many partitions? NaN
      error: 'NaN' is not a valid number
      Start again :-(
      
      Should I create a new topic? [y/n]: y
      How many partitions? 2
      Created topic topic-2.
      
      Should I create a new topic? [y/n]: n
      all topics = topic-0,topic-1,topic-2
      
      ... # producer will be started

### 2. Status
In another terminal, check the status

    ./status.sh <consumer-group>

Example:

      ./status.sh beta1

      Date: 2022-08-07T12:19:24.260758 - consumerGroup: beta1

      Topic             Partition   currentOffset   endOffset        Client Id	instanceId
      topic-0           0           82552           115740           DOG-sssss	consumer-0
      topic-0           1           82734           123495           DOG-sssss	consumer-0
      topic-0           2           82829           124778           DOG-sssss	consumer-0
      topic-0           3           83019           132598           DOG-sssss	consumer-0
      topic-0           4           82159           104783           DOG-sssss	consumer-0
      topic-1           0           90748           102191           DOG-sssss	consumer-0
      topic-1           1           82012           111033           DOG-sssss	consumer-0
      topic-1           2           91547           121748           DOG-sssss	consumer-0
      topic-1           3           64296           130536           DOG-sssss	consumer-0
      topic-1           4           60149           135886           DOG-lllll	consumer-1
      topic-2           0           45272           101025           DOG-sssss	consumer-0
      topic-2           1           44761           111701           DOG-lllll	consumer-1
      topic-2           2           54954           121007           DOG-sssss	consumer-0
      topic-2           3           73511           130747           DOG-lllll	consumer-1
      topic-2           4           73653           136913           DOG-lllll	consumer-1
      topic-3           0           72045           95246            DOG-lllll	consumer-1
      topic-3           1           72594           107364           DOG-lllll	consumer-1
      topic-3           2           73044           121535           DOG-lllll	consumer-1
      topic-3           3           73271           132366           DOG-lllll	consumer-1
      topic-3           4           73589           144882           DOG-lllll	consumer-1
      topic-4           0           72036           95205            DOG-lllll	consumer-1
      topic-4           1           72662           110642           DOG-lllll	consumer-1
      topic-4           2           36530           119117           DOG-lllll	consumer-1
      topic-4           3           36726           132765           DOG-sssss	consumer-0
      topic-4           4           36807           143664           DOG-lllll	consumer-1
      
      CLIENT           PARTITIONS
      DOG-lllll        topic-1(4) topic-2(1,3,4) topic-3(0,1,2,3,4) topic-4(0,1,2,4)
      DOG-sssss        topic-0(0,1,2,3,4) topic-1(0,1,2,3) topic-2(0,2) topic-4(3)

### 3. Consumers

And in another terminal, launch the consumer group

     ./launch-consumer.sh <strategy> <initial consumers> <consumer-group> <static-assignment>

Strategies can be:
* round: https://kafka.apache.org/32/javadoc/org/apache/kafka/clients/consumer/RoundRobinAssignor.html
* range: https://kafka.apache.org/32/javadoc/org/apache/kafka/clients/consumer/RangeAssignor.html
* sticky: https://kafka.apache.org/32/javadoc/org/apache/kafka/clients/consumer/StickyAssignor.html
* coop: https://kafka.apache.org/32/javadoc/org/apache/kafka/clients/consumer/CooperativeStickyAssignor.html

`static-assignment` can be true or false (false by default) (timeout is 10 seconds)

Example:

     ./launch-consumer.sh range 3 r3 true
     consumer-0 launched [16568]
     consumer-1 launched [16569]
     consumer-2 launched [16570]

     What's next? [a] Add new consumer, [k] kill last consumer, [e] exit: a
     consumer-3 launched [16573]

     What's next? [a] Add new consumer, [k] kill last consumer, [e] exit: a
     consumer-4 launched [16574]

     What's next? [a] Add new consumer, [k] kill last consumer, [e] exit: k
     Give the consumer number you want to kill:  2
     Consumer 2 killed (pid 16570) - Date: Wed Aug 10 23:47:08 CEST 2022

     What's next? [a] Add new consumer, [k] kill last consumer, [e] exit: e
     Time to clean up!
     Consumer 0 killed (pid 16568) - Date: Wed Aug 10 23:47:17 CEST 2022
     Consumer 1 killed (pid 16569) - Date: Wed Aug 10 23:47:17 CEST 2022
     No pid found for consumer 2.
     Consumer 3 killed (pid 16573) - Date: Wed Aug 10 23:47:17 CEST 2022
     Consumer 4 killed (pid 16574) - Date: Wed Aug 10 23:47:17 CEST 2022

### Clean up

1. Stop all scripts
2. Run

    docker-compose down -v

# References:
- https://medium.com/streamthoughts/understanding-kafka-partition-assignment-strategies-and-how-to-write-your-own-custom-assignor-ebeda1fc06f3

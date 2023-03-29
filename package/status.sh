#!/usr/bin/env bash

if [[ $# -lt 1 ]] ; then
    echo 'Provide the consumer groups to check status'
    echo "$0 <consumer-groups> "
    exit 0
fi

CONSUMER_GROUP=$1
LAG_THRESHOLD=$2

#watch -n 0.2 "date && \
#           kafka-consumer-groups --bootstrap-server localhost:29092 --group $CONSUMER_GROUP --describe --members --verbose && \
#           kafka-consumer-groups --bootstrap-server localhost:29092 --group $CONSUMER_GROUP --describe --offsets --verbose"

java -cp  partitioning-tool-1.0.0-SNAPSHOT-jar-with-dependencies.jar  partitioning.tool.kafka.admin.ConsumerGroupStatus config.properties "$CONSUMER_GROUP" "$LAG_THRESHOLD"

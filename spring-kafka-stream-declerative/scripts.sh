cddocker #!/bin/bash

# create input topic with two partitions
bin/kafka-topics.sh --create \
  --zookeeper localhost:2181 \
  --replication-factor 1 \
  --partitions 2 \
  --topic word-count-input

# create output topic
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic q-out/

# launch a Kafka consumer
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic q-out --from-beginning  --formatter kafka.tools.DefaultMessageFormatter  --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# launch the streams application

# Console data producer
kafka-console-producer --broker-list localhost:9092 --topic q-in

# package your application as a fat jar
mvn clean package

# run your fat jar
java -jar <your jar here>.jar

# list all topics that we have in Kafka (so we can observe the internal topics)
bin/kafka-topics.sh --list --zookeeper localhost:2181

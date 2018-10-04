package com.github.ymen.kafkaqs.services;

import com.github.ymen.kafkaqs.configuration.SinkStreamsProcessor;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * A Demo consumer service
 */

@Service
@EnableBinding(SinkStreamsProcessor.class)

public class ExampleWordCountService {

    /*

    kafka-console-producer --broker-list localhost:9092 --topic q-in5
     */
    @StreamListener("input")
    void process(KStream<Object, String> input) {

        input.mapValues(String::toLowerCase)
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .selectKey((key, value) -> value)
                .groupByKey()
                .count()
                .toStream()
                .print(Printed.toSysOut())


        ;
    }
}
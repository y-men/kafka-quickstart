package com.github.ymen.kafkaqs.configuration;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public interface MessageStreamBinder {
    String IN = "qqin";
    String OUT = "qqout";

    @Output(IN)
    KStream<String, String> output();

    @Input(OUT)
    KStream<String, String> input();
}

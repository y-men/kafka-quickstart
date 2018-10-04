package com.github.ymen.kafkaqs.configuration;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;


/**
 * Sink only interface to bind to
 */
public interface SinkStreamsProcessor {
    @Input("input")
    KStream<?, ?> input();
}



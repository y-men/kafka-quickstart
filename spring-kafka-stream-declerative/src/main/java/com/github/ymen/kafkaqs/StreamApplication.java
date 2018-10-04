package com.github.ymen.kafkaqs;

import com.github.ymen.kafkaqs.configuration.SinkStreamsProcessor;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.messaging.Processor;


/*
TODO
- grandle/ grunt to copy jars from .m folder
 */
@SpringBootApplication
public class StreamApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreamApplication.class, args);
    }
}

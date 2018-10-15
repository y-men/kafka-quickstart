package bringg.kafka.stream;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventStreamIngestor {
//    @Bean
//    public KTable<String,Long> driversTable( StreamsBuilder streamsBuilder){
//        ////StreamsBuilder builder = new StreamsBuilder();
//        streamsBuilder.table( "")
//
//    }

    public static void main(String[] args) {
        SpringApplication.run(EventStreamIngestor.class, args);
    }
}

/*
TODO
- Use auto version advance
 */
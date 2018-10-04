package b.kafka.stream.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

//@Slf4j
//@Service
//@EnableBinding(Processor.class)

public class ProcessorService {

    // @Autowired
    private TimeWindows timeWindows;

    public ProcessorService(TimeWindows timeWindows) {
        this.timeWindows = timeWindows;
    }

    @StreamListener(Processor.INPUT)
        // @SendTo(Processor.OUTPUT)
//    KStream<Windowed<String>, Long>
    void process(KStream<Object, String> input) {

        //  return
        input
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                //.map((key, value) -> new KeyValue<>(value, value))
                .selectKey((key, value) -> value)
//                .groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .windowedBy(timeWindows)
                .count()
                //Materialized.as("WordCounts-1"))
                .toStream()
                .print(Printed.toSysOut())
        ;
        //   .peek( (key,value) -> log.debug(">>>  " +value ))
        ;
        //
        // .map((key, value) -> new KeyValue<>(null, new KafkaStreamsWordCountApplication.WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))));
    }


}

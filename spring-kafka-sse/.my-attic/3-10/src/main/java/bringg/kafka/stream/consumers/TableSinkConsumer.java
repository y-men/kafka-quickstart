package bringg.kafka.stream.consumers;

import bringg.kafka.stream.configurations.MultiSinkProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableBinding(MultiSinkProcessor.class)

public class TableSinkConsumer {
    /**
     * This needs to be re-written as a connector to a relevnat storage
     */
//    @Autowired
//    private InteractiveQueryService  interactiveQueryService;
//    @Autowired
//    StreamsBuilder streamsBuilder;
//
//    @Autowired
//    StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    //@StreamListener()
    public void readDriversInfo(@Input("out") KTable<String, Long> out) {
        System.out.println(" >> readDriversInfo");

        out.toStream().print( Printed.toSysOut());//.peek((k, v) -> System.out.println("####>>>> readDriversInfo " + v));
////        out.
//        // log.debug( ">>> readDriversInfo ");
//        System.out.println(" >>> readDriversInfo ");
//        // Interactive Queries APIs
//        //ut.to
//        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
//        final String queryableStoreName = out.queryableStoreName();
//        System.out.println(" ########>>> queryableStoreName ");
//
//        if ( queryableStoreName == null) return;
//        ReadOnlyKeyValueStore view = kafkaStreams.store(queryableStoreName, QueryableStoreTypes.keyValueStore());
//        view.all().forEachRemaining((e) -> System.out.println(">>>" + e));

        //view.get(key);
//        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("active-drivers-with-tasks")
//                                 .withKeySerde(Serdes.String())
//                                 .withValueSerde(Serdes.Long())


    }

}

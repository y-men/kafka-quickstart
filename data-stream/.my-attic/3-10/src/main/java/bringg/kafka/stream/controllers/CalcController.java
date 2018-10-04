package bringg.kafka.stream.controllers;

import bringg.kafka.stream.configurations.MultiSinkProcessor;
import bringg.kafka.stream.domain.CalcResult;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@EnableBinding(MultiSinkProcessor.class)
@RequestMapping("calc")
public class CalcController {
    @Autowired
    StreamsBuilder streamsBuilder;
//
    @Autowired
    StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Autowired
    QueryableStoreRegistry queryableStoreRegistry;

    @GetMapping(value = "/{cmd}", produces = "application/json")
    public @ResponseBody  CalcResult[] getCalcResult(@PathVariable String cmd) throws InterruptedException {
        if( cmd == null || cmd.equals("")) return null;

        List<CalcResult> results = new ArrayList<CalcResult>();

//        KTable<String, Long> table = streamsBuilder
      //  StreamsBuilder builder = new StreamsBuilder();
//        if( table == null){
//        }

//        KTable<String, Long> table = streamsBuilder.table("qstore",
//                Materialized.as("queryableStore")
////                Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("queryableStore")
////                        .withKeySerde(Serdes.String())
////                        .withValueSerde(Serdes.Long())
//
//        );



        System.out.print(">>>>");
//        table.toStream().print(Printed.toSysOut());
        ReadOnlyKeyValueStore view;
        view = queryableStoreRegistry.getQueryableStoreType( "qstore",QueryableStoreTypes.keyValueStore());

//        try {
//
//            view = streamsBuilderFactoryBean.getKafkaStreams().store("qstore",QueryableStoreTypes.keyValueStore() );
//        } catch (Exception e) {
//            System.out.println("--" + e);
//
//        }
        System.out.println("view= " + view);
        view.all().forEachRemaining( (entry) ->{
            results.add( new CalcResult((String)((KeyValue) entry).key,(Long)((KeyValue) entry).value));
        });


        // --------------
//        KTable<String, Long> table = streamsBuilder.table("out1",
//                Materialized.as("queryableStore")
////                Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("queryableStore")
////                        .withKeySerde(Serdes.String())
////                        .withValueSerde(Serdes.Long())
//
//        );
//        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
//        final String queryableStoreName = table.queryableStoreName();
//
//        System.out.println(" ######");
//
//        if (queryableStoreName != null) {
//            System.out.println(" ########>>> queryableStoreName ---------------------");
//
//            ReadOnlyKeyValueStore view =
//                    kafkaStreams.store(queryableStoreName, QueryableStoreTypes.keyValueStore());
//            //        waitUntilStoreIsQueryable(queryableStoreName, QueryableStoreTypes.keyValueStore(),kafkaStreams );
//            view.all().forEachRemaining((e) -> {
//                System.out.println(">>>" + e);
//                //results.add( new CalcResult( ))
//            });
//
//        }
        return results.toArray(new CalcResult[0]);


    }

    public static <T> T waitUntilStoreIsQueryable(final String storeName,
                                                  final QueryableStoreType<T> queryableStoreType,
                                                  final KafkaStreams streams) throws InterruptedException {
        while (true) {
            try {
                return streams.store(storeName, queryableStoreType);
            } catch (InvalidStateStoreException ignored) {
                // store not yet ready for querying
                Thread.sleep(100);
            }
        }
    }

}

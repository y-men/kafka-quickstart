package bringg.kafka.stream.configurations;


import bringg.kafka.stream.model.Shift;
import bringg.kafka.stream.model.Task;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfiguration {

    private boolean alreadyRegistered = false;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "bringg");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "bringg-client");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return new StreamsConfig(props);
    }


    @Bean
    public KStream<String, Task> qstoreStream(StreamsBuilder streamsBuilder) {
        //   if( alreadyRegistered ) return null;
        KStream<String, Task> tasks = streamsBuilder.stream("tasks", Consumed.with(Serdes.String(), new JsonSerde<>(Task.class))
                .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));
        KStream<String, Shift> shifts = streamsBuilder.stream("shifts", Consumed.with(Serdes.String(), new JsonSerde<>(Shift.class)));
        Set<Long> set = new HashSet<>();

        tasks.map((k, v) -> new KeyValue<>(String.valueOf(v.getTaskId()), v.getUserId()))
                .groupByKey()
                .aggregate(() -> " ", (v, v2, ag) -> v2)
                .toStream().to("tasks-users-table", Produced.with(Serdes.String(), Serdes.String()));
//
//        ;
//
//
//
//
//
//
//        tasks.mapValues((v)-> v.getTaskId())
//                .groupByKey()
//
//                // Count distinct tasks
//                .aggregate(() -> {
//                            set.clear();
//                            return 0L;
//                        },
//                        (s, aLong, aLong2) -> {
//                            set.add(aLong);
//                            return Long.valueOf(set.size());
//                        },
//                        Materialized.with(Serdes.String(), Serdes.Long())
//                )
//                .toStream().to("tasks-users-table", Produced.with(Serdes.String(), Serdes.Long()));

        //       KTable<Long, String> tasksUsersTable =  streamsBuilder.table("tasks-users-table",Consumed.with( Serdes.Long(),Serdes.String()));
        //      KTable <String,Long> usersTasks  =

//                tasksUsersTable
        //               .toStream( (k,v)-> new KeyValue( String.valueOf(v),k))
//                .peek( (k,v) -> System.out.println("1 TTTTTTT "  + k))
//
//                .map( (k,v)-> new KeyValue<>(String.valueOf(v),k))
//                .peek( (k,v) -> System.out.println("2 TTTTTTT "  + k))
//
//                .groupByKey( Serialized.with( Serdes.String(),Serdes.Long()))
//                .count()
        ;

        shifts.map((k, s) -> new KeyValue<>(s.getShift().getUserId(), s))
                .peek((k, v) -> System.out.println("1 >>>>> " + k))

                .filter((k, v) -> isActiveNow(v))
                .mapValues((v) -> 1L)
                .peek((k, v) -> System.out.println("2 >>>>> " + k))

                //           .join(usersTasks, (v1,v2)->v2 , Joined.with( Serdes.String(),new JsonSerde<>(Shift.class),Serdes.Long()))
                .peek((k, v) -> System.out.println("3 >>>>> " + k))
                .to("qstore2", Produced.with(Serdes.String(), Serdes.Long()));
//
//
//
//                ;
//
//                shifts.leftJoin( tasksUsersTable,(shift,nu)->{
//                    if (isActiveNow(shift)) {
//                        return
//                    }
//                    else return null;
//
//                    }
//                    )
//                .peek((k, v) -> System.out.println(" ## 2 SHIFTS ,k: " + k + " v: " + v))
//
//                //With tasks, retain 'null' in none active - to remove
//                .join(tasksUsersTable, (sw, l) -> isActiveNow(sw) ? l : 0L,
//                        Joined.with(Serdes.String(), new JsonSerde<>(Shift.class), Serdes.Long()))
//                .peek((k, v) -> System.out.println(" ## 3 SHIFTS ,k: " + k + " v: " + v))
//
//                //TODO Make a join for user-names too/ extract constant table/ bootstrap
//                .groupByKey()
//                .reduce((v, a) -> {
//
//                            //Create a tombstone
//                            // TODO: Why null values are lost in group...
//                            if (a == 0 || a == null) return null;
//                            else return a;
//                        },
//                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("qstore2")
//                                .withKeySerde(Serdes.String())
//                                .withValueSerde(Serdes.Long())
//
//                )


//        shifts.map((k, s) -> new KeyValue<>(s.getShift().getUserId(), s))
//
//                .peek((k, v) -> System.out.println(" ## 2 SHIFTS ,k: " + k + " v: " + v))
//
//                //With tasks, retain 'null' in none active - to remove
//                .join(tasksUsersTable, (sw, l) -> isActiveNow(sw) ? l : 0L,
//                        Joined.with(Serdes.String(), new JsonSerde<>(Shift.class), Serdes.Long()))
//                .peek((k, v) -> System.out.println(" ## 3 SHIFTS ,k: " + k + " v: " + v))
//
//                //TODO Make a join for user-names too/ extract constant table/ bootstrap
//                .groupByKey()
//                .reduce((v, a) -> {
//
//                            //Create a tombstone
//                            // TODO: Why null values are lost in group...
//                            if (a == 0 || a == null) return null;
//                            else return a;
//                        },
//                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("qstore2")
//                                .withKeySerde(Serdes.String())
//                                .withValueSerde(Serdes.Long())
//
//                )
        ;

        return tasks;
    }

// -------------------

    private boolean isActiveNow(Shift sw) {
        Date d = new Date();
        if (sw == null) return false;
        if ((sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift() == null)
                || (sw.getShift().getEndShift().compareTo(d) == 1 && sw.getShift().getStartShift() == null)
                || (sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift().compareTo(d) == 1))
            return true;
        else return false;
    }


}

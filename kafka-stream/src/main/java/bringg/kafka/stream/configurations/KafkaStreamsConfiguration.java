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


    /**
     * Query the stream of tasks and join it with shifts that are currently active
     * @param streamsBuilder
     * @return
     */
    @Bean
    public KStream<String, Shift> qstoreStream(StreamsBuilder streamsBuilder) {
        KTable<String, Task> tasks = streamsBuilder.table( "tasks", Consumed.with(Serdes.String(), new JsonSerde<>(Task.class)) );

        KStream<String, Shift> shifts = streamsBuilder.stream("shifts", Consumed.with(Serdes.String(), new JsonSerde<>(Shift.class)));
        Set<Task> set = new HashSet();
        KTable<String, Long>
                distinctTaskCountTable =
                tasks.groupBy((k, t) -> new KeyValue<>(k, t), Serialized.with(Serdes.String(), new JsonSerde<>(Task.class)))
                        .aggregate(() -> 1L,
                                (k, t, a) -> {
                                    set.add(t);
                                    return Long.valueOf(set.size());
                                }
                                , (k, t, a) -> {
                                    set.remove(t);
                                    return Long.valueOf(set.size());
                                }
                                , Materialized.with(Serdes.String(), Serdes.Long())
                        );

        shifts
                .map((k, sw) -> new KeyValue<>(sw.getShift().getUserId(), sw))
                .peek((k, v) -> System.out.println(" ## 2 SHIFTS ,k: " + k + " v: " + v))
                .mapValues( (v)-> isActiveNow(v) ? 1L : 0L )
                .peek((k, v) -> System.out.println(" ## 3 SHIFTS ,k: " + k + " v: " + v))

                //TODO Make a join for user-names too/ extract constant table/ bootstrap
                // TODO Enrich the data with tasks data
                .groupByKey( Serialized.with(Serdes.String(), Serdes.Long()))
                .reduce((v, a) -> {

                            //Create a tombstone
                            // TODO: Why null values are lost in group...
                            if (a == 0 || a == null) return null;
                            else return a;
                        },
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("query-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Long())

                )
        ;

        return shifts;
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

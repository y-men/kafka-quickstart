package bringg.kafka.stream.consumers;

import bringg.kafka.stream.configurations.MultiSinkProcessor;
import bringg.kafka.stream.deserializers.ShiftsSerde;
import bringg.kafka.stream.deserializers.TaskSerde;
import bringg.kafka.stream.model.ShiftWrapper;
import bringg.kafka.stream.model.Task;
import lombok.Data;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;


import java.util.*;

/**
 * Consumer service
 */
/*
TODO
-JSON serialization
- Schema registry
 */

@Service
@EnableBinding(MultiSinkProcessor.class)

public class EventConsumer {

    @Autowired
    StreamsBuilderFactoryBean streamsBuilderFactoryBean;


    @StreamListener
    /**
     * Driver need to be on shift and have tasks assigned otherwise it is zero
     */
    void activeDrivers(@Input("tasks") KStream<Object, Task> tasks,
                       @Input("shifts") KStream<Object, ShiftWrapper> shifts) {
        System.out.println(" >> driversOnShiftWithTasksAssigned");
        KTable<String, Long> kTable = null;
        Set<Long> set = new HashSet<>();

        kTable =
                tasks
                        .map((k, v) -> new KeyValue<>(v.getUserId(), v.getTaskId()))
                        // todo: Check repartition here...
                       // .map((k, v) -> new KeyValue<>(Bytes.toString(), v.getTaskId()))
                        .peek((k, v) -> System.out.println(" ## 1 TASKS >> k:  " + k + " v:  " + v))
                        .groupByKey(Serialized.with(Serdes.String(), Serdes.Long()))
                        // .reduce( (v,agg)-> v+agg,(v,agg)-> agg-v,)
                        .aggregate(() -> {
                                    set.clear();
                                    return 0L;
                                },
                                (s, aLong, aLong2) -> {
                                    set.add(aLong);
                                    return Long.valueOf(set.size());
                                },
                                //aLong2++,
                                Materialized.with(Serdes.String(), Serdes.Long())

                        )
        //.toStream().print(Printed.toSysOut());
        ;

        //Todo: Is it more correct to move this to a different method ?
        shifts
                .map((k, sw) -> new KeyValue<>(sw.getShift().getUserId(), sw))
                .peek((k, v) -> System.out.println(" ## 2 SHIFTS ,k: " + k + " v: " + v))

                //With tasks, retain 'null' in none active - to remove
                .join(kTable, (sw, l) -> isActiveNow(sw) ? l : 0L,
                        Joined.with(Serdes.String(), new ShiftsSerde(), Serdes.Long()))
                .peek((k, v) -> System.out.println(" ## 3 SHIFTS ,k: " + k + " v: " + v))

                //TODO Make a join for user-names too/ extract constant table/ bootstrap
                .groupByKey()
                .reduce((v, a) -> {

                            //Create a tombstone
                            // TODO: Why null values are lost in group...
                            if (a == 0 || a == null) return null;
                            else return a;
                        },
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("qstore2")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Long())

                )
        ;
    }

    // @StreamListener
    void driversOnShiftWithTasksAssigned(@Input("tasks") KStream<Object, Task> tasks,
                                         @Input("shifts") KStream<Object, ShiftWrapper> shifts) {

        System.out.println(" >> driversOnShiftWithTasksAssigned");
//        final Serde<String> stringSerde = Serdes.String();
//        final Serde<Long> longSerde = Serdes.Long();

        KStream<String, Task> modifiedTasksStream =
                tasks
                        .peek((k, v) -> System.out.println(" 1. TASKS >> k:  " + k + " v:  " + v))
                        .map((k, v) -> new KeyValue<>(v.getUserId(), v));

        //  KTable<String, Long> table =
        shifts
                .peek((k, sw) -> System.out.println(" 1. >> SHIFTS ,key ---> " + k))

                .map((k, sw) -> new KeyValue<>(sw.getShift().getUserId(), sw))
                .peek((k, sw) -> System.out.println(" 2  >> SHIFTS ,key ---> " + k + " value -->" + sw))
                .filter((origin, sw) -> {
                            return isActiveNow(sw);
                        }

                )
                //Remove not active from table

                //  .map( (k,v) -> new KeyValue<String,ShiftWrapper>("nk",v))
                .peek((k, sw) -> System.out.println(" 3  >> SHIFT ,key ---> " + k + " value -->" + sw))

                // ---------
                .join(modifiedTasksStream,
                        (v, vo) -> 1L,
                        JoinWindows.of(1000 * 60 * 60),
                        Joined.with(Serdes.String(), new ShiftsSerde(), new TaskSerde()))
                // TODO: Need to uderstand why the below does not work...
                //                         Joined.with(Serdes.String(), new JsonSerde<ShiftWrapper>(), new JsonSerde<Task>()))
                .groupByKey(Serialized.with(Serdes.String(), Serdes.Long()))
                .count(
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("qstore")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Long())
                )
        ;

        // Debug
        //.peek((k, v) -> System.out.println(" 4.  TABLE  k: " + k + "-  v: " + v))
    }

    private boolean isActiveNow(ShiftWrapper sw) {
        Date d = new Date();
        if (sw == null) return false;
        if ((sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift() == null)
                || (sw.getShift().getEndShift().compareTo(d) == 1 && sw.getShift().getStartShift() == null)
                || (sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift().compareTo(d) == 1))
            return true;
        else return false;
    }


    /**
     * Consume the topics
     * <code>    kafka-console-producer --broker-list localhost:9092 --topic q-in5</code>
     *
     * @param tasksStream
     * @param shiftsStream
     */
    //  @StreamListener
    void process(@Input("tasks") KStream<?, Task> tasksStream,
                 @Input("shifts") KStream<?, ShiftWrapper> shiftsStream) {

        tasksStream
                .peek((k, task) -> System.out.println(" task.getTaskId() ---> " + task.getTaskId()))
                .print(Printed.toSysOut());
        shiftsStream
                .peek((k, shift) -> System.out.println(" shift.getShiftId() ---> " + shift.getShiftId()))
                .print(Printed.toSysOut());
    }


}
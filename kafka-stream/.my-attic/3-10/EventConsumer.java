package bringg.kafka.stream.consumers;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                            Date d = new Date();
                            if (sw == null) return false;
                            if ((sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift() == null)
                                    || (sw.getShift().getEndShift().compareTo(d) == 1 && sw.getShift().getStartShift() == null)
                                    || (sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift().compareTo(d) == 1))
                                return true;
                            else return false;
                        }

                )
                //  .map( (k,v) -> new KeyValue<String,Shift>("nk",v))
                .peek((k, sw) -> System.out.println(" 3  >> SHIFT ,key ---> " + k + " value -->" + sw))

                // ---------
                .join(modifiedTasksStream,
                        (v, vo) -> 1L,
                        JoinWindows.of(1000 * 60 * 60),
                        Joined.with(Serdes.String(), new ShiftsSerde(), new TaskSerde()))
                // TODO: Need to uderstand why the below does not work...
                //                         Joined.with(Serdes.String(), new JsonSerde<Shift>(), new JsonSerde<Task>()))
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


    /**
     * @param tasks
     * @param shifts
     */
    //@StreamListener
    void driversOnShift(@Input("tasks") KStream<Object, Task> tasks,
                        @Input("shifts") KStream<Object, ShiftWrapper> shifts) {

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        shifts
                .peek((k, sw) -> System.out.println(" 1. >> shift,key ---> " + k))

                .map((k, sw) -> new KeyValue<>(sw.getShift().getUserId(), sw))
                .peek((k, sw) -> System.out.println(" 2  >> shift,key ---> " + k + " value -->" + sw))
                .filter((origin, sw) -> {
                            Date d = new Date();
                            if (sw == null) return false;
                            if ((sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift() == null)
                                    || (sw.getShift().getEndShift().compareTo(d) == 1 && sw.getShift().getStartShift() == null)
                                    || (sw.getShift().getStartShift().compareTo(d) == -1 && sw.getShift().getEndShift().compareTo(d) == 1))
                                return true;
                            else return false;
                        }

                )
                //  .map( (k,v) -> new KeyValue<String,Shift>("nk",v))
                .peek((k, sw) -> System.out.println(" 3  >> shift,key ---> " + k + " value -->" + sw))
                .groupByKey(Serialized.with(Serdes.String(), new ShiftsSerde()))
                .aggregate(() -> 1L, (k, v, agg) -> 1L,
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("out-8")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Long())
                )

                // Debug
                .toStream()
                .peek((k, v) -> System.out.println(" 4.  TABLE  k: " + k + "-  v: " + v))

        ;

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
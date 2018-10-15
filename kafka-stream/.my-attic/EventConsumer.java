package bringg.kafka.stream.consumers;

import bringg.kafka.stream.model.ShiftWrapper;
import bringg.kafka.stream.model.Task;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.properties.KafkaStreamsBindingProperties;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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


    @StreamListener
    void countTasksAssignedToActiveDriver(@Input("tasks") KStream<Object, Task> tasks,
                                          @Input("shifts") KStream<Object, ShiftWrapper> shifts) {

       // KStream<String, Long> tasksToActiveDriver =
                tasks.peek((k, task) -> System.out.println(" task.getTaskId() ---> " + task.getTaskId()))
                       .join( shifts,(t,v)-> t+" " + v, JoinWindows.of(TimeUnit.MINUTES.toSeconds(10)))

                        //.join(shifts, (tv, sv) ->tv.getUserId().equals(sv.getShift().getUserId()) )
                        .print(Printed.toSysOut())
                ;


        //Count tasks assigned to drivers on shift
        // map keys
        // join
        // reduce/ count

    }


    /**
     * Consume the topics
     * <code>    kafka-console-producer --broker-list localhost:9092 --topic q-in5</code>
     *
     * @param tasksStream
     * @param shiftsStream
     */
//    @StreamListener
    void process(@Input("tasks") KStream<?, Task> tasksStream,
                 @Input("shifts") KStream<?, ShiftWrapper> shiftsStream) {

        tasksStream
                .peek((k, task) -> System.out.println(" task.getTaskId() ---> " + task.getTaskId()))
                .print(Printed.toSysOut());
        shiftsStream
                .peek((k, shift) -> System.out.println(" shift.getShiftId() ---> " + shift.getShiftId()))
                .print(Printed.toSysOut());


        //Count tasks assigned to drivers on shift

    }

    /*

    kafka-console-producer --broker-list localhost:9092 --topic q-in5
     */
//    @StreamListener("input")
//    void process(KStream<Object, String> input) {
//
//        input.mapValues(String::toLowerCase)
//                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .selectKey((key, value) -> value)
//                .groupByKey()
//                .count()
//                .toStream()
//                .print(Printed.toSysOut())
//
//
//        ;
//    }
}
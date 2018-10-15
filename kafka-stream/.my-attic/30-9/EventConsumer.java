package bringg.kafka.stream.consumers;

import bringg.kafka.stream.deserializers.ShiftsSerde;
import bringg.kafka.stream.model.ShiftWrapper;
import bringg.kafka.stream.model.Task;
import lombok.Data;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.properties.KafkaStreamsBindingProperties;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
                                          //@Input("shifts") KTable<Object, Shift> shifts) {
                                          @Input("shifts") KStream<Object, ShiftWrapper> shifts) {

//        KStream<String, Shift> mappedShiftStream =
        // KTable<String, List<ShiftPeriodEntry>> table =
        shifts
                .peek((k, sw) -> System.out.println(" s1>> shift,key ---> " + k))

                .map((k, sw) -> new KeyValue<>(sw.getShift().getUserId(), sw))
                .peek((k, sw) -> System.out.println(" s2>> shift,key ---> " + k))

                .groupByKey(Serialized.with(Serdes.String(), new ShiftsSerde()))
//                .count()
//                .toStream().print(Printed.toSysOut())

                .aggregate(
                         ShiftAggregator::new
                        ,(k,v,agg) -> agg.add(v)
                        //                        (key, shiftWrapper, vr) -> {
//                            vr.add(shiftWrapper);
////                            Date startDate = shiftWrapper.getShift().getStartShift();
////                            Date endDate = shiftWrapper.getShift().getEndShift();
////                            aggregate.add(new ShiftPeriodEntry ( startDate, endDate) );
//                            return vr;
//                        }
                        // ,Materialized.with(Serdes.String(), new JsonSerde<List>() )
//                        ,Materialized.<String, List<Shift>, WindowStore<Bytes, byte[]>>as("result-store")
//                                .withKeySerde( Serdes.String())
//                                .withValueSerde(new JsonSerde<>())
                       , Materialized.<String, ShiftAggregator, KeyValueStore<Bytes, byte[]>>with(Serdes.String(),new ShiftAggregatorSerde())
//                                Serdes.serdeFrom(ShiftAggregator.class))

//                        Materialized.<String, List<Shift>, SessionStore<Bytes, byte[]>>
//                                as("log-input-stream-aggregated")
//                                .withKeySerde(Serdes.String())
//                                .withValueSerde(new JsonSerde<>())



                )
        ;

//        tasks.peek((k, task) -> System.out.println("t1>> task.getTaskId() ---> " + task.getTaskId()))
//                .map((k, t) -> new KeyValue<>(t.getUserId(), t))
//                .join( table,(t,shiftsForUser )->shiftsForUser)
//
//                // Tasks assigned to drivers that are currently on shift
//                .filter( (id,shiftTimes)->{
//                    Date d = new Date();
//                    Object[] r = shiftTimes.stream().filter( (pe) -> pe.startDate.compareTo(d) == -1 || pe.startDate.compareTo(d) ==1 ).toArray();
//                    return r!= null && r.length >0 ;
//
//
//                })
////                .mapValues( (l)->l.size())
//                .groupByKey()
//
//                //Number of tasks for each driver on shift
//                .aggregate(()-> 1,(k,v,a)-> a++)
//
//                // Debug
//                .toStream()
//                .print(Printed.toSysOut())
        ;
        ;

    }


    /**
     * Consume the topics
     * <code>    kafka-console-producer --broker-list localhost:9092 --topic q-in5</code>
     *
     * @param tasksStream
     * @param shiftsStream
     */
    // @StreamListener
    void process(@Input("tasks") KStream<?, Task> tasksStream,
                 @Input("shifts") KStream<?, ShiftWrapper> shiftsStream) {

        tasksStream
                .peek((k, task) -> System.out.println(" task.getTaskId() ---> " + task.getTaskId()))
                .print(Printed.toSysOut());
        shiftsStream
                .peek((k, shift) -> System.out.println(" shift.getShiftId() ---> " + shift.getShiftId()))
                .print(Printed.toSysOut());
    }

    // ----------------------------------------------------

    @Data
    private class ShiftPeriodEntry {
        private Date startDate;
        private Date endDate;

        public ShiftPeriodEntry(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private class ShiftAggregatorSerde extends JsonSerde<ShiftAggregator>{}
    private class ShiftAggregator {
        private List<ShiftWrapper> l = new ArrayList<>();
        public ShiftAggregator add ( ShiftWrapper s){
            l.add(s);
            return this;
        }
    }
}
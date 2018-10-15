package bringg.kafka.stream.configurations;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface MultiSinkProcessor  extends SingleSinkProcessor{
    @Input("tasks")
    KStream< ?,?> tasks();

    @Input("shifts")
    KStream< ?,?> shifts();

    @Input("qstore")
    KTable<?,?> qstore();
//    @Input("out")
//    KTable< ?,?> out();


}

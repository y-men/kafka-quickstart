package bringg.kafka.stream.configurations;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface SingleSinkProcessor {
    @Input("input")
    KStream<?, ?> input();
}

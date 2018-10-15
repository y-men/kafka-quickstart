package bringg.kafka.stream.consumers;


import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@EnableBinding(MultiSink.class)
public class TaskConsumer {


}

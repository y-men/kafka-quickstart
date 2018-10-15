package bringg.kafka.stream.deserializers;

import bringg.kafka.stream.model.Task;
import org.springframework.kafka.support.serializer.JsonSerde;

public class TaskSerde extends JsonSerde<Task>
{
}

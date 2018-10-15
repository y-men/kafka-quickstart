package bringg.kafka.stream.deserializers;

import bringg.kafka.stream.model.ShiftWrapper;
import org.springframework.kafka.support.serializer.JsonSerde;

public class ShiftsSerde extends JsonSerde<ShiftWrapper> {
}

package bringg.kafka.stream.configurations;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.internals.StreamThread;
import org.springframework.cglib.util.StringSwitcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfuguration {
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "bringg");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.LongSerde.class);

        return new StreamsConfig(props);
    }

    // Configure for weblfux data streaming
    @Bean
    public KafkaReceiver kafkaReceiver( StreamsConfig streamsConfig ) {
        Map<String, Object> configProps = new HashMap<>();
                //streamsConfig.getConsumerConfigs( StreamThread..create())
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  org.apache.kafka.common.serialization.StringDeserializer.class);
        configProps.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.LongDeserializer.class);
        configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "appid-consumer");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "appid-group");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions receiverOptions = ReceiverOptions
                .create(configProps ).subscription(Pattern.compile("qstore2"));

        DefaultKafkaReceiver defaultKafkaReceiver = new DefaultKafkaReceiver(ConsumerFactory.INSTANCE, receiverOptions   );
        return defaultKafkaReceiver;
    }

}

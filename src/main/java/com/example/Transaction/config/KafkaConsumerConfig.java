package com.example.Transaction.config;

import com.example.Transaction.dto.request.OrderTransactionRequest;
import com.example.Transaction.dto.request.UserBalanceRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    // Consumer factory for OrderTransactionRequest
    @Bean
    public ConsumerFactory<String, OrderTransactionRequest> paymentRequestConsumerFactory() {
        JsonDeserializer<OrderTransactionRequest> deserializer =
                new JsonDeserializer<>(OrderTransactionRequest.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "txn-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderTransactionRequest> paymentRequestKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderTransactionRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentRequestConsumerFactory());
        return factory;
    }

    // Consumer factory for UserBalanceRequest
    @Bean
    public ConsumerFactory<String, UserBalanceRequest> balanceRequestConsumerFactory() {
        JsonDeserializer<UserBalanceRequest> deserializer =
                new JsonDeserializer<>(UserBalanceRequest.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "txn-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserBalanceRequest> balanceRequestKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserBalanceRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(balanceRequestConsumerFactory());
        return factory;
    }
}

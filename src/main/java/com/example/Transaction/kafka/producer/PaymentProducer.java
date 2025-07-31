package com.example.Transaction.kafka.producer;

import com.example.Transaction.dto.response.UpdatedOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, UpdatedOrderResponse> kafkaTemplate;

    public void sendPaymentResult(UpdatedOrderResponse updatedOrderResponse) {
        kafkaTemplate.send("payment-response", updatedOrderResponse);
    }
}

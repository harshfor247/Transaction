package com.example.Transaction.kafka.producer;

import com.example.Transaction.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, TransactionResponse> kafkaTemplate;

    public void sendPaymentResult(TransactionResponse transactionResponse) {
        kafkaTemplate.send("payment-response", transactionResponse);
    }
}

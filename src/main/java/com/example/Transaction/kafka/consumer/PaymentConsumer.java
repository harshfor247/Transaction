package com.example.Transaction.kafka.consumer;

import com.example.Transaction.dto.request.TransactionRequest;
import com.example.Transaction.dto.response.TransactionResponse;
import com.example.Transaction.kafka.producer.PaymentProducer;
import com.example.Transaction.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final PaymentProducer producer;

    @KafkaListener(topics = "payment-request", groupId = "txn-group")
    public void listen(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = paymentService.processPayment(transactionRequest);
        producer.sendPaymentResult(transactionResponse);
    }
}

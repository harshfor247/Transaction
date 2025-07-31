package com.example.Transaction.kafka.consumer;

import com.example.Transaction.dto.request.OrderTransactionRequest;
import com.example.Transaction.dto.request.TransactionRequest;
import com.example.Transaction.dto.response.TransactionResponse;
import com.example.Transaction.entity.OrderTransaction;
import com.example.Transaction.kafka.producer.PaymentProducer;
import com.example.Transaction.repository.OrderTransactionRepository;
import com.example.Transaction.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final OrderTransactionRepository orderTransactionRepository;
    private final PaymentProducer producer;

    @KafkaListener(topics = "payment-request", groupId = "txn-group", containerFactory = "paymentRequestKafkaListenerFactory")
    public void listen(OrderTransactionRequest orderTransactionRequest) {
        OrderTransaction orderTransaction = new OrderTransaction();
        orderTransaction.setUserId(orderTransactionRequest.getUserId());
        orderTransaction.setOrderId(orderTransactionRequest.getOrderId());
        orderTransaction.setAmount(orderTransactionRequest.getAmount());
        orderTransaction.setOrderPayment(orderTransactionRequest.getOrderPayment());
        orderTransactionRepository.save(orderTransaction);
        log.info("Listening for payment for user {}", orderTransactionRequest.getUserId());
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionResponse transactionResponse = paymentService.processPayment(transactionRequest);
        producer.sendPaymentResult(transactionResponse);
    }
}

package com.example.Transaction.service;

import com.example.Transaction.dto.request.TransactionRequest;
import com.example.Transaction.dto.response.TransactionResponse;
import com.example.Transaction.dto.response.UserBalanceUpdatedResponse;
import com.example.Transaction.enums.PaymentMode;
import com.example.Transaction.enums.PaymentStatus;
import com.example.Transaction.kafka.consumer.BalanceConsumer;
import com.example.Transaction.kafka.producer.BalanceProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BalanceConsumer balanceConsumer;
    private final BalanceProducer balanceProducer;

    public TransactionResponse processPayment(TransactionRequest transactionRequest) {
        Long userId = transactionRequest.getUserId();
        PaymentMode paymentMode = transactionRequest.getPaymentMode();
        double price = transactionRequest.getAmount();

        if (paymentMode == null || price <= 0 || price > 100000) {
            return buildResponse(transactionRequest.getOrderId(), PaymentStatus.FAILURE);
        }

        Double userBalance = balanceConsumer.getBalanceMap().get(userId);

        if (userBalance == null || userBalance < price) {
            return buildResponse(transactionRequest.getOrderId(), PaymentStatus.FAILURE);
        }

        boolean isSuccess = Math.random() > 0.2;

        if (isSuccess) {
            double remaining = userBalance - price;

            // Update in map
            balanceConsumer.getBalanceMap().put(userId, remaining);

            // Send to balanceResponse topic
            UserBalanceUpdatedResponse userBalanceUpdatedResponse = UserBalanceUpdatedResponse.builder()
                    .userId(userId)
                    .updatedUserBalance(remaining)
                    .build();
            balanceProducer.sendUpdatedBalance(userBalanceUpdatedResponse);
        }

        return buildResponse(transactionRequest.getOrderId(), isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE);
    }

    private TransactionResponse buildResponse(Long orderId, PaymentStatus paymentStatus) {
        return TransactionResponse.builder()
                .orderId(orderId)
                .paymentStatus(paymentStatus)
                .build();
    }
}
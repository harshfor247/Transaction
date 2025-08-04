package com.example.Transaction.service;

import com.example.Transaction.dto.request.TransactionRequest;
import com.example.Transaction.dto.response.TransactionResponse;
import com.example.Transaction.dto.response.UpdatedOrderResponse;
import com.example.Transaction.dto.response.UserBalanceUpdatedResponse;
import com.example.Transaction.entity.OrderTransaction;
import com.example.Transaction.entity.Transaction;
import com.example.Transaction.entity.UserBalance;
import com.example.Transaction.enums.PaymentMode;
import com.example.Transaction.enums.PaymentStatus;
import com.example.Transaction.exceptions.PaymentAlreadyDoneException;
import com.example.Transaction.exceptions.TransactionFailedException;
import com.example.Transaction.kafka.producer.BalanceProducer;
import com.example.Transaction.kafka.producer.PaymentProducer;
import com.example.Transaction.repository.OrderTransactionRepository;
import com.example.Transaction.repository.TransactionRepository;
import com.example.Transaction.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final BalanceProducer balanceProducer;
    private final PaymentProducer paymentProducer;

    public TransactionResponse processPayment(TransactionRequest transactionRequest) {
        Long userId = transactionRequest.getUserId();
        Long orderId = transactionRequest.getOrderId();
        Double amount = transactionRequest.getAmount();
        PaymentMode paymentMode = transactionRequest.getPaymentMode();

        // Check if transaction already exists for this order
        List<Transaction> transaction = transactionRepository.findByOrderId(orderId);
        if (transaction != null && !transaction.isEmpty()) {
            for (Transaction txn : transaction) {
                PaymentStatus status = txn.getPaymentStatus();
                if (status.equals(PaymentStatus.SUCCESS) || status == null) {
                    throw new PaymentAlreadyDoneException("Payment already done or no such Order!");
                }
            }
        }

        // Fetch all OrderTransactions by user
        List<OrderTransaction> userOrders = orderTransactionRepository.findByUserId(userId);
        if (userOrders == null || userOrders.isEmpty()) {
            saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
            throw new TransactionFailedException("Payment Failed due to no such order!");
        }

        // Filter order by orderId and amount
        OrderTransaction matchingOrder = userOrders.stream()
                .filter(o -> o.getOrderId().equals(orderId) && o.getAmount().equals(amount))
                .findFirst()
                .orElse(null);

        if (matchingOrder == null) {
            saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
            throw new TransactionFailedException("Payment Failed due to invalid order Id or invalid amount!");
        }


        // Basic validations
        if (paymentMode == null || amount <= 0) {
            saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
            throw new TransactionFailedException("Payment Failed due to invalid payment mode or amount!");
        }

        // Check user balance
        Optional<UserBalance> optional = userBalanceRepository.findById(userId);
        if (optional.isEmpty()) {
            saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
            throw new TransactionFailedException("Payment Failed as user not  found!");
        }

        UserBalance userBalance = optional.get();
        Double currentBalance = userBalance.getBalance();

        if(currentBalance < amount){
            saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
            throw new TransactionFailedException("Payment Failed due to insufficient balance!");
        }

        // Simulate payment success (80% chance)
        boolean isSuccess = Math.random() > 0.2;
        PaymentStatus status = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE;

        TransactionResponse response = saveAndBuildResponse(transactionRequest, status);

        if (isSuccess) {
            Double remaining = currentBalance - amount;
            userBalance.setBalance(remaining);

            userBalanceRepository.save(userBalance);

            UserBalanceUpdatedResponse updatedResponse = UserBalanceUpdatedResponse.builder()
                    .userId(userId)
                    .updatedUserBalance(remaining)
                    .build();

            balanceProducer.sendUpdatedBalance(updatedResponse);

            return response;
        } else{
            throw new TransactionFailedException("Payment Failed! Try again later.");
        }
    }

    private TransactionResponse saveAndBuildResponse(TransactionRequest req, PaymentStatus status) {
        Transaction saved = transactionRepository.save(Transaction.builder()
                .userId(req.getUserId())
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .paymentMode(req.getPaymentMode())
                .paymentStatus(status)
                .build());

        UpdatedOrderResponse updatedOrderResponse = new UpdatedOrderResponse();

        updatedOrderResponse.setTransactionId(saved.getTransactionId());
        updatedOrderResponse.setUserId(saved.getUserId());
        updatedOrderResponse.setOrderId(saved.getOrderId());
        updatedOrderResponse.setAmount(saved.getAmount());
        updatedOrderResponse.setPaymentStatus(status);

        paymentProducer.sendPaymentResult(updatedOrderResponse);

        return TransactionResponse.builder()
                .transactionId(saved.getTransactionId())
                .userId(saved.getUserId())
                .orderId(saved.getOrderId())
                .amount(saved.getAmount())
                .paymentStatus(saved.getPaymentStatus())
                .build();
    }
}

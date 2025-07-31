package com.example.Transaction.service;

import com.example.Transaction.dto.request.TransactionRequest;
import com.example.Transaction.dto.response.TransactionResponse;
import com.example.Transaction.dto.response.UserBalanceUpdatedResponse;
import com.example.Transaction.entity.OrderTransaction;
import com.example.Transaction.entity.Transaction;
import com.example.Transaction.entity.UserBalance;
import com.example.Transaction.enums.PaymentMode;
import com.example.Transaction.enums.PaymentStatus;
import com.example.Transaction.kafka.producer.BalanceProducer;
import com.example.Transaction.repository.OrderTransactionRepository;
import com.example.Transaction.repository.TransactionRepository;
import com.example.Transaction.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final BalanceProducer balanceProducer;

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
                if (PaymentStatus.SUCCESS.equals(status) || status == null) {
                    throw new RuntimeException("Payment already done or status unknown for the order");
                }
            }
        }

        // Fetch all OrderTransactions by user
        List<OrderTransaction> userOrders = orderTransactionRepository.findByUserId(userId);
        if (userOrders == null || userOrders.isEmpty()) {
            return saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
        }

        // Filter order by orderId and amount
        OrderTransaction matchingOrder = userOrders.stream()
                .filter(o -> o.getOrderId().equals(orderId) && o.getAmount().equals(amount))
                .findFirst()
                .orElse(null);

        if (matchingOrder == null) {
            return saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
        }

        // Basic validations
        if (paymentMode == null || amount <= 0 || amount > 100000) {
            return saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
        }

        // Check user balance
        Optional<UserBalance> optional = userBalanceRepository.findById(userId);
        if (optional.isEmpty() || optional.get().getBalance() < amount) {
            return saveAndBuildResponse(transactionRequest, PaymentStatus.FAILURE);
        }

        UserBalance userBalance = optional.get();
        double currentBalance = userBalance.getBalance();

        // Simulate payment success (80% chance)
        boolean isSuccess = Math.random() > 0.2;
        PaymentStatus status = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILURE;

        if (isSuccess) {
            double remaining = currentBalance - amount;
            userBalance.setBalance(remaining);
            userBalanceRepository.save(userBalance);

            UserBalanceUpdatedResponse updatedResponse = UserBalanceUpdatedResponse.builder()
                    .userId(userId)
                    .updatedUserBalance(remaining)
                    .build();

            balanceProducer.sendUpdatedBalance(updatedResponse);
        }

        return saveAndBuildResponse(transactionRequest, status);
    }

    private TransactionResponse saveAndBuildResponse(TransactionRequest req, PaymentStatus status) {
        Transaction saved = transactionRepository.save(Transaction.builder()
                .userId(req.getUserId())
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .paymentMode(req.getPaymentMode())
                .paymentStatus(status)
                .build());

        return TransactionResponse.builder()
                .transactionId(saved.getTransactionId())
                .userId(saved.getUserId())
                .orderId(saved.getOrderId())
                .amount(saved.getAmount())
                .paymentStatus(saved.getPaymentStatus())
                .build();
    }
}

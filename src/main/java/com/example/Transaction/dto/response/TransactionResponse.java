package com.example.Transaction.dto.response;

import com.example.Transaction.enums.PaymentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionResponse {

    private Long transactionId;
    private Long userId;
    private Long orderId;
    private Double amount;
    private PaymentStatus paymentStatus;
}
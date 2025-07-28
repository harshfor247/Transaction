package com.example.Transaction.dto.response;

import com.example.Transaction.enums.PaymentMode;
import com.example.Transaction.enums.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long transactionID;
    private Long userId;
    private Long orderId;
    private Double amount;
    private PaymentStatus paymentStatus;
}
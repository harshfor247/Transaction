package com.example.Transaction.dto.request;

import com.example.Transaction.enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    private Long userId;
    private Long orderId;
    private Double amount;
    private PaymentMode paymentMode;
}
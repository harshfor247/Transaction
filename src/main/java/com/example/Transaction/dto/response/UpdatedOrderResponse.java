package com.example.Transaction.dto.response;

import com.example.Transaction.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatedOrderResponse {

    private Long transactionId;
    private Long userId;
    private Long orderId;
    private Double amount;
    private PaymentStatus paymentStatus;
}

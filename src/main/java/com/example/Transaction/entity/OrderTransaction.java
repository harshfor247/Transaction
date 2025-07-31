package com.example.Transaction.entity;

import com.example.Transaction.enums.OrderPayment;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderTransaction {

    @Id
    private Long userId;

    private Long orderId;
    private Double amount;
    private OrderPayment orderPayment;
}

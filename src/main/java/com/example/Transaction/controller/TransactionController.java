package com.example.Transaction.controller;

import com.example.Transaction.dto.request.TransactionRequest;
import com.example.Transaction.dto.response.TransactionResponse;
import com.example.Transaction.enums.PaymentStatus;
import com.example.Transaction.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<TransactionResponse> pay(@RequestBody TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = paymentService.processPayment(transactionRequest);
        return ResponseEntity.ok(transactionResponse);
    }
}

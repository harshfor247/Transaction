package com.example.Transaction.kafka.producer;

import com.example.Transaction.dto.response.UserBalanceUpdatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceProducer {

    private final KafkaTemplate<String, UserBalanceUpdatedResponse> kafkaTemplate;

    public void sendUpdatedBalance(UserBalanceUpdatedResponse userBalanceUpdatedResponse) {
        kafkaTemplate.send("balance-response", userBalanceUpdatedResponse.getUserId().toString(), userBalanceUpdatedResponse);
    }
}

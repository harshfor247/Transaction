package com.example.Transaction.kafka.consumer;

import com.example.Transaction.dto.request.UserBalanceRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@Getter
public class BalanceConsumer {

    private final ConcurrentHashMap<Long, Double> balanceMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "balance-request", groupId = "txn-group")
    public void consumeBalance(UserBalanceRequest userBalanceRequest) {
        log.info("Received balance for user {}: {}", userBalanceRequest.getUserId(), userBalanceRequest.getUserBalance());
        balanceMap.put(userBalanceRequest.getUserId(), userBalanceRequest.getUserBalance());
    }
}

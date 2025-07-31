package com.example.Transaction.kafka.consumer;

import com.example.Transaction.dto.request.UserBalanceRequest;
import com.example.Transaction.entity.UserBalance;
import com.example.Transaction.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class BalanceConsumer {

    private final UserBalanceRepository userBalanceRepository;

    @KafkaListener(topics = "balance-request", groupId = "txn-group", containerFactory = "balanceRequestKafkaListenerFactory")
    public void consumeBalance(UserBalanceRequest userBalanceRequest) {
        log.info("Consumed balance event: {}", userBalanceRequest);

        UserBalance balanceEntity = UserBalance.builder()
                .userId(userBalanceRequest.getUserId())
                .balance(userBalanceRequest.getUserBalance())
                .build();

        userBalanceRepository.save(balanceEntity);
        log.info("User balance saved/updated in DB for userId: {}", userBalanceRequest.getUserId());
    }
}

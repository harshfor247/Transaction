package com.example.Transaction.repository;

import com.example.Transaction.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
}

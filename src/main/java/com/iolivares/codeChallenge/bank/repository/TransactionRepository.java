package com.iolivares.codeChallenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iolivares.codeChallenge.bank.model.repository.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{

}

package com.iolivares.codeChallenge.bank.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.iolivares.codeChallenge.bank.model.repository.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String>{
	
	List<Transaction> findByAccount_iban(String account_iban, Sort sort);

}

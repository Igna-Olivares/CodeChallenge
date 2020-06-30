package com.iolivares.codeChallenge.bank.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iolivares.codeChallenge.bank.model.repository.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String>{
	
	@Query("SELECT c FROM Transaction c WHERE (:accountIban is null or c.accountIban = :accountIban)")
	List<Transaction> findByAccountIban(@Param("accountIban") String accountIban, Sort sort);

}

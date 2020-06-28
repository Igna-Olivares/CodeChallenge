package com.iolivares.codeChallenge.bank.service;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.service.Transaction;
import com.iolivares.codeChallenge.bank.model.service.TransactionStatus;

public interface TransactionService {

	public void createTransaction(CreateTransactionCommand newtransaction);
	
	public List<Transaction> searchTransactions(String iban, Direction direction);
	
	public TransactionStatus searchTransactionStatus(String reference, String channel);
	
	
}

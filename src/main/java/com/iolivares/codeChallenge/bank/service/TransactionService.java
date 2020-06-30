package com.iolivares.codeChallenge.bank.service;

import java.util.List;

import com.iolivares.codeChallenge.bank.enumerations.TransactionChannels;
import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.service.Transaction;
import com.iolivares.codeChallenge.bank.model.service.TransactionStatus;

public interface TransactionService {

	public Transaction createTransaction(CreateTransactionCommand newtransaction);
	
	public List<Transaction> searchTransactions(String iban, String direction);
	
	public TransactionStatus searchTransactionStatus(String reference, TransactionChannels channel);
	
	
}

package com.iolivares.codeChallenge.bank.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.iolivares.codeChallenge.bank.model.api.AmountDirectionEnum;
import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.repository.Account;
import com.iolivares.codeChallenge.bank.model.service.Transaction;
import com.iolivares.codeChallenge.bank.model.service.TransactionStatus;
import com.iolivares.codeChallenge.bank.repository.AccountRepository;
import com.iolivares.codeChallenge.bank.repository.TransactionRepository;
import com.iolivares.codeChallenge.bank.service.TransactionService;
import com.iolivares.codeChallenge.bank.validators.CreateTransactionValidator;
import com.iolivares.codeChallenge.common.exceptions.TechnicalException;

import lombok.Setter;
import ma.glasnost.orika.MapperFacade;

public class TransactionServiceImpl implements TransactionService {
	
	@Setter
	@Autowired
	private MapperFacade defaultMapper;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired 
	private AccountRepository accountRepository;
	
	@Autowired
	private CreateTransactionValidator transactionValidator;

	@Override
	public void createTransaction(CreateTransactionCommand newtransaction) {
		
		
		Account account = accountRepository.findByIban(newtransaction.getAccount_iban());
		if(account == null) {
			throw new TechnicalException("There is no account associated with that IBAN", HttpStatus.SC_NOT_FOUND);
		}
		List<String> errorList = transactionValidator.validate(newtransaction, account.getBalance());
		if(CollectionUtils.isNotEmpty(errorList)) {
			throw new TechnicalException("Create Transaction validation error", HttpStatus.SC_UNPROCESSABLE_ENTITY, errorList);
		}
		
		transactionRepository.save(defaultMapper.map(newtransaction, com.iolivares.codeChallenge.bank.model.repository.Transaction.class));

	}

	@Override
	public List<Transaction> searchTransactions(String iban, AmountDirectionEnum direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionStatus searchTransactionStatus(String reference, String channel) {
		// TODO Auto-generated method stub
		return null;
	}

}

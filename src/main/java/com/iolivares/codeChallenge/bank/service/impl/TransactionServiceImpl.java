package com.iolivares.codeChallenge.bank.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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
		
		if(StringUtils.isNotEmpty(newtransaction.getReference()) && !validateReference(newtransaction.getReference())){
			throw new TechnicalException("Create Transaction reference already exist", HttpStatus.SC_UNPROCESSABLE_ENTITY, errorList);
		}else {
			newtransaction.setReference(generateReference());
		}
		
		transactionRepository.save(defaultMapper.map(newtransaction, com.iolivares.codeChallenge.bank.model.repository.Transaction.class));

	}

	@Override
	public List<Transaction> searchTransactions(String iban, Direction direction) {
		
		return defaultMapper.mapAsList(transactionRepository.findByAccount_iban(iban, Sort.by(direction, "amount")), Transaction.class);
	}

	@Override
	public TransactionStatus searchTransactionStatus(String reference, String channel) {
		// TODO Auto-generated method stub
		return null;
	}
	
	///////////////////
	//PRIVATE METHODS//
	///////////////////
	
	private String generateReference() {
		boolean isValid = false;
		String randomReference = null;
		while(isValid != true) {
			randomReference = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
			isValid = validateReference(randomReference);
		}
		return randomReference;
	}

	private boolean validateReference(String randomReference) {
		return transactionRepository.findById(randomReference) == null;
	}
}

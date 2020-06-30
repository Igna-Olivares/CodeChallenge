package com.iolivares.codeChallenge.bank.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.repository.Account;
import com.iolivares.codeChallenge.bank.model.service.Transaction;
import com.iolivares.codeChallenge.bank.model.service.TransactionStatus;
import com.iolivares.codeChallenge.bank.repository.AccountRepository;
import com.iolivares.codeChallenge.bank.repository.TransactionRepository;
import com.iolivares.codeChallenge.bank.service.TransactionService;
import com.iolivares.codeChallenge.bank.validators.CreateTransactionValidator;
import com.iolivares.codeChallenge.common.exceptions.TechnicalException;
import com.iolivares.codeChallenge.common.utils.DateUtils;

import lombok.Setter;
import ma.glasnost.orika.MapperFacade;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static final int AFTER_TODAY = 1;
	private static final int EQUALS_TODAY = 0;
	private static final int BEFORE_TODAY = -1;

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
	public void createTransaction(CreateTransactionCommand newTransaction) {

		List<String> errorList = transactionValidator.validate(newTransaction);
		if (CollectionUtils.isNotEmpty(errorList)) {
			throw new TechnicalException("Create Transaction validation error", HttpStatus.SC_UNPROCESSABLE_ENTITY,	errorList);
		}
		Account account = accountRepository.findByIban(newTransaction.getAccount_iban());
		if (account == null) {
			throw new TechnicalException("There is no account associated with that IBAN", HttpStatus.SC_NOT_FOUND);
		}
		List<String> accountErrorList = transactionValidator.validateAccountBalance(newTransaction, account.getBalance());
		if (CollectionUtils.isNotEmpty(accountErrorList)) {
			throw new TechnicalException("Create Transaction balance validation error", HttpStatus.SC_UNPROCESSABLE_ENTITY,	accountErrorList);
		}

		if (StringUtils.isNotEmpty(newTransaction.getReference())
				&& !validateReference(newTransaction.getReference())) {
			throw new TechnicalException("Create Transaction reference already exist",
					HttpStatus.SC_UNPROCESSABLE_ENTITY, errorList);
		} else {
			newTransaction.setReference(generateReference());
		}

		transactionRepository.save(
				defaultMapper.map(newTransaction, com.iolivares.codeChallenge.bank.model.repository.Transaction.class));

	}


	@Override
	public List<Transaction> searchTransactions(String iban, Direction direction) {

		return defaultMapper.mapAsList(transactionRepository.findByAccountIban(iban, Sort.by(direction, "amount")),
				Transaction.class);
	}

	@Override
	public TransactionStatus searchTransactionStatus(String reference, String channel) {

		Optional<com.iolivares.codeChallenge.bank.model.repository.Transaction> repositoryTransaction = transactionRepository
				.findById(reference);

		TransactionStatus response = new TransactionStatus();
		response.setReference(reference);
		if (!repositoryTransaction.isPresent()) {
			response.setInvalidStatus();
		} else {
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction = repositoryTransaction.get();
			int dateValue = DateUtils.compareDatesToNow(transaction.getDate());
			switch (dateValue) {
				case BEFORE_TODAY:
					caseBeforeToday(channel, response, transaction);
					break;
				case EQUALS_TODAY:
					caseEqualsToday(channel, response, transaction);
					break;
				case AFTER_TODAY:
					caseAfterToday(channel, response, transaction);
					break;
			}
		}

		return response;
	}

	///////////////////
	// PRIVATE METHODS//
	///////////////////

	private String generateReference() {
		boolean isValid = false;
		String randomReference = null;
		while (isValid != true) {
			randomReference = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
			isValid = validateReference(randomReference);
		}
		return randomReference;
	}

	private boolean validateReference(String randomReference) {
		return !transactionRepository.findById(randomReference).isPresent();
	}


	private void caseAfterToday(String channel, TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		if (isClient(channel)) {
			response.setFutureStatus();
			fillResponseWithAmountLessFee(response, transaction);
		} else if (isAtm(channel)) {
			response.setPendingStatus();
			fillResponseWithAmountLessFee(response, transaction);
		} else if (isInternal(channel)) {
			response.setFutureStatus();
			fillResponseWithAmountAndFee(response, transaction);
		}
	}

	private void caseEqualsToday(String channel, TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		response.setPendingStatus();
		if (isClientOrAtm(channel)) {
			fillResponseWithAmountLessFee(response, transaction);
		} else if (isInternal(channel)) {
			fillResponseWithAmountAndFee(response, transaction);
		}
	}

	private void caseBeforeToday(String channel, TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		response.setSettledStatus();
		if (isClientOrAtm(channel)) {
			fillResponseWithAmountLessFee(response, transaction);
		} else if (isInternal(channel)) {
			fillResponseWithAmountAndFee(response, transaction);
		}
	}
	
	private boolean isAtm(String channel) {
		return StringUtils.equals(channel, "ATM");
	}

	private boolean isClient(String channel) {
		return StringUtils.equals(channel, "CLIENT");
	}

	private boolean isInternal(String channel) {
		return StringUtils.equals(channel, "INTERNAL");
	}

	private boolean isClientOrAtm(String channel) {
		return isClient(channel) || isAtm(channel);
	}
	
	private void fillResponseWithAmountLessFee(TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		response.setAmount(transaction.getAmount() - transaction.getFee());
	}

	private void fillResponseWithAmountAndFee(TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		response.setAmount(transaction.getAmount());
		response.setFee(transaction.getFee());
	}
}

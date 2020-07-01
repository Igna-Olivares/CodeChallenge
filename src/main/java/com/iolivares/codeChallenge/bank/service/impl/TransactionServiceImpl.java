package com.iolivares.codeChallenge.bank.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.iolivares.codeChallenge.bank.enumerations.TransactionChannels;
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

import ma.glasnost.orika.MapperFacade;

@Service
public class TransactionServiceImpl implements TransactionService {


	private final MapperFacade defaultMapper;

	private final TransactionRepository transactionRepository;

	private final AccountRepository accountRepository;

	private final CreateTransactionValidator transactionValidator;

	public TransactionServiceImpl(MapperFacade defaultMapper, TransactionRepository transactionRepository,
			AccountRepository accountRepository, CreateTransactionValidator transactionValidator) {
		this.defaultMapper = defaultMapper;
		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
		this.transactionValidator = transactionValidator;
	}

	@Override
	public Transaction createTransaction(CreateTransactionCommand newTransaction) {

		// * 1. Validate the required values and formats *//
		List<String> errorList = transactionValidator.validate(newTransaction);
		if (CollectionUtils.isNotEmpty(errorList)) {
			throw new TechnicalException("Create Transaction validation error", HttpStatus.SC_UNPROCESSABLE_ENTITY,
					errorList);
		}

		// * 2. Find the existence of the destination account *//
		Account account = accountRepository.findByIban(newTransaction.getAccount_iban());
		if (account == null) {
			throw new TechnicalException("There is no account associated with that IBAN", HttpStatus.SC_NOT_FOUND);
		}

		// * 3. Validate if the balance could be less than 0 *//
		List<String> accountErrorList = transactionValidator.validateAccountBalance(newTransaction,
				account.getBalance());
		if (CollectionUtils.isNotEmpty(accountErrorList)) {
			throw new TechnicalException("Create Transaction balance validation error",
					HttpStatus.SC_UNPROCESSABLE_ENTITY, accountErrorList);
		}

		// * 4. Check if they give us the reference and validate that it does not
		// already exist *//
		if (StringUtils.isNotEmpty(newTransaction.getReference())
				&& !validateReference(newTransaction.getReference())) {
			throw new TechnicalException("Create Transaction reference already exist",
					HttpStatus.SC_UNPROCESSABLE_ENTITY, errorList);
		} else if (StringUtils.isEmpty(newTransaction.getReference())) {
			newTransaction.setReference(generateReference());
		}

		// * 5. Map to the repository object and check if it is necessary to generate
		// the transaction date. Update the value of te balance of the account *//
		com.iolivares.codeChallenge.bank.model.repository.Transaction transactionToCreate = defaultMapper
				.map(newTransaction, com.iolivares.codeChallenge.bank.model.repository.Transaction.class);
		if (transactionToCreate.getDate() == null) {
			transactionToCreate.setDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		account.setBalance(account.getBalance() + (newTransaction.getAmount() - newTransaction.getFee()));

		// * 6. Save the object and update the account balance *//
		com.iolivares.codeChallenge.bank.model.repository.Transaction transactionSaved = transactionRepository
				.save(transactionToCreate);
		accountRepository.save(account);

		// * 7. Return the new transaction object *//
		return defaultMapper.map(transactionSaved, Transaction.class);

	}

	@Override
	public List<Transaction> searchTransactions(String iban, Direction direction) {

		// * 1. Set up the sorting if it is necessary *//
		Sort sort = null;
		if (direction != null) {
			sort = Sort.by(direction, "amount");
		}
		// * 2. Find the transactions *//
		return defaultMapper.mapAsList(transactionRepository.findByAccountIban(iban, sort), Transaction.class);
	}

	@Override
	public TransactionStatus searchTransactionStatus(String reference, TransactionChannels channel) {

		// * 1. Check if the transaction exist *//
		Optional<com.iolivares.codeChallenge.bank.model.repository.Transaction> repositoryTransaction = transactionRepository
				.findById(reference);

		TransactionStatus response = new TransactionStatus();
		response.setReference(reference);
		if (!repositoryTransaction.isPresent()) {
			response.setInvalidStatus();
		} else {
			// * 2. If the transaction exists, enter the business logic *//
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction = repositoryTransaction.get();
			int dateValue = DateUtils.compareDatesToNow(transaction.getDate());
			if (dateValue < 0) { //If the date is before today
				caseBeforeToday(channel, response, transaction);
			} else if (dateValue == 0) { //If the date is equals today
				caseEqualsToday(channel, response, transaction);
			} else if (dateValue > 0) { //If the date is after today
				caseAfterToday(channel, response, transaction);
			}
		}

		return response;
	}

	///////////////////
	// PRIVATE METHODS//
	///////////////////

	/**
	 * Generates a random alphanumeric String of size 6 until is valid as reference
	 * for transaction
	 * 
	 * @return String - new unique reference
	 */
	private String generateReference() {
		boolean isValid = false;
		String randomReference = null;
		while (isValid != true) {
			randomReference = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
			isValid = validateReference(randomReference);
		}
		return randomReference;
	}

	/**
	 * Check if the reference given is used for another transaction
	 * 
	 * @return Boolean - true if is not present
	 */
	private Boolean validateReference(String randomReference) {
		return !transactionRepository.findById(randomReference).isPresent();
	}

	/**
	 * Fill the transaction status in the case of his date is after today
	 * 
	 */
	private void caseAfterToday(TransactionChannels channel, TransactionStatus response,
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

	/**
	 * Fill the transaction status in the case of his date is equals to today
	 * 
	 */
	private void caseEqualsToday(TransactionChannels channel, TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		response.setPendingStatus();
		if (isClientOrAtm(channel)) {
			fillResponseWithAmountLessFee(response, transaction);
		} else if (isInternal(channel)) {
			fillResponseWithAmountAndFee(response, transaction);
		}
	}

	/**
	 * Fill the transaction status in the case of his date is before today
	 * 
	 */
	private void caseBeforeToday(TransactionChannels channel, TransactionStatus response,
			com.iolivares.codeChallenge.bank.model.repository.Transaction transaction) {
		response.setSettledStatus();
		if (isClientOrAtm(channel)) {
			fillResponseWithAmountLessFee(response, transaction);
		} else if (isInternal(channel)) {
			fillResponseWithAmountAndFee(response, transaction);
		}
	}

	private boolean isAtm(TransactionChannels channel) {
		return channel != null && channel.equals(TransactionChannels.ATM);
	}

	private boolean isClient(TransactionChannels channel) {
		return channel != null && channel.equals(TransactionChannels.CLIENT);
	}

	private boolean isInternal(TransactionChannels channel) {
		return channel != null && channel.equals(TransactionChannels.INTERNAL);
	}

	private boolean isClientOrAtm(TransactionChannels channel) {
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

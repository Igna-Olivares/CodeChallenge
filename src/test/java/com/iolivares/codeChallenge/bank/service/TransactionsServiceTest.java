package com.iolivares.codeChallenge.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.iolivares.codeChallenge.CodeChallengeApplication;
import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.repository.Account;
import com.iolivares.codeChallenge.bank.model.repository.Transaction;
import com.iolivares.codeChallenge.bank.repository.AccountRepository;
import com.iolivares.codeChallenge.bank.repository.TransactionRepository;
import com.iolivares.codeChallenge.bank.service.impl.TransactionServiceImpl;
import com.iolivares.codeChallenge.bank.validators.CreateTransactionValidator;
import com.iolivares.codeChallenge.common.configuration.OrikaConfiguration;
import com.iolivares.codeChallenge.common.exceptions.TechnicalException;
import com.iolivares.codeChallenge.common.utils.DateUtils;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = CodeChallengeApplication.class)
public class TransactionsServiceTest {

	OrikaConfiguration orikaConfiguration = new OrikaConfiguration();

	@InjectMocks
	private TransactionServiceImpl transactionService;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private CreateTransactionValidator transactionValidator;

	@Before
	public void setUp() {
		transactionService.setDefaultMapper(orikaConfiguration.defaultMapper());
	}

	@Test
	public void testCreateTransactionOk() {

		// Given
		Account mockedAccount = new Account();
		mockedAccount.setBalance(5000.0);
		mockedAccount.setIban("ES9820385778983000760236");
		mockedAccount.setHolder("Ignacio");

		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");

		// When
		when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
		when(transactionRepository.findById(anyString())).thenReturn(null);
		transactionService.createTransaction(newTransaction);

		// Then
		ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionRepository).save(transactionCaptor.capture());
		Transaction createdTransaction = transactionCaptor.getValue();
		assertThat(createdTransaction.getReference()).isNotNull();
		assertEquals(createdTransaction.getAccount_iban(), "ES9820385778983000760236");
		assertEquals(createdTransaction.getDate(), DateUtils.StringDateToLong("2019-07-16T16:55:42.000Z"));
		assertEquals(createdTransaction.getAmount(), 193.38, 0.01);
		assertEquals(createdTransaction.getFee(), 3.18, 0.01);
		assertEquals(createdTransaction.getDescription(), "Restaurant payment");
	}

	@Test
	public void testCreateTransactionNotExistingAccount() {

		// Expected exception
		TechnicalException exception = Assertions.assertThrows(TechnicalException.class, () -> {

			// Given
			CreateTransactionCommand newTransaction = new CreateTransactionCommand();
			newTransaction.setReference("12345A");
			newTransaction.setAccount_iban("ES9820385778983000760236");
			newTransaction.setDate("2019-07-16T16:55:42.000Z");
			newTransaction.setAmount(193.38);
			newTransaction.setFee(3.18);
			newTransaction.setDescription("Restaurant payment");

			// When
			when(accountRepository.findByIban(anyString())).thenReturn(null);
			transactionService.createTransaction(newTransaction);
		});

		assertTrue(exception.getMessage().contains("There is no account associated with that IBAN"));

	}

	@Test
	public void testCreateTransactionValidationError() {

		// Expected exception
		TechnicalException exception = Assertions.assertThrows(TechnicalException.class, () -> {

			// Given
			Account mockedAccount = new Account();
			mockedAccount.setBalance(5000.0);
			mockedAccount.setIban("ES9820385778983000760236");
			mockedAccount.setHolder("Ignacio");

			CreateTransactionCommand newTransaction = new CreateTransactionCommand();
			newTransaction.setReference("12345A");
			newTransaction.setAccount_iban("");
			newTransaction.setDate("2019-07-16T16:55:42.000Z");
			newTransaction.setFee(3.18);
			newTransaction.setDescription("Restaurant payment");

			// When
			when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
			when(transactionValidator.validate(newTransaction, 5000.0))
					.thenReturn(Arrays.asList("The Amount is required"));
			transactionService.createTransaction(newTransaction);

		});

		assertTrue(exception.getMessage().contains("Create Transaction validation error"));
	}

	@Test
	public void testCreateTransactionReferenceError() {

		// Expected exception
		TechnicalException exception = Assertions.assertThrows(TechnicalException.class, () -> {

			// Given
			Account mockedAccount = new Account();
			mockedAccount.setBalance(5000.0);
			mockedAccount.setIban("ES9820385778983000760236");
			mockedAccount.setHolder("Ignacio");
			Transaction mockedTransaction = new Transaction();

			CreateTransactionCommand newTransaction = new CreateTransactionCommand();
			newTransaction.setReference("12345A");
			newTransaction.setAccount_iban("");
			newTransaction.setDate("2019-07-16T16:55:42.000Z");
			newTransaction.setFee(3.18);
			newTransaction.setDescription("Restaurant payment");

			// When
			when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
			when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
			transactionService.createTransaction(newTransaction);
		});

		assertTrue(exception.getMessage().contains("Create Transaction reference already exist"));

	}

}

package com.iolivares.codeChallenge.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.iolivares.codeChallenge.bank.model.service.Transaction;
import com.iolivares.codeChallenge.bank.model.service.TransactionStatus;
import com.iolivares.codeChallenge.bank.repository.AccountRepository;
import com.iolivares.codeChallenge.bank.repository.TransactionRepository;
import com.iolivares.codeChallenge.bank.service.impl.TransactionServiceImpl;
import com.iolivares.codeChallenge.bank.validators.CreateTransactionValidator;
import com.iolivares.codeChallenge.common.configuration.OrikaConfiguration;
import com.iolivares.codeChallenge.common.exceptions.TechnicalException;
import com.iolivares.codeChallenge.common.utils.DateUtils;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = CodeChallengeApplication.class)
public class TransactionsServiceTest {
	
	private static final PodamFactory factory = new PodamFactoryImpl();

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
		when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());
		transactionService.createTransaction(newTransaction);

		// Then
		ArgumentCaptor<com.iolivares.codeChallenge.bank.model.repository.Transaction> transactionCaptor = ArgumentCaptor.forClass(com.iolivares.codeChallenge.bank.model.repository.Transaction.class);
		verify(transactionRepository).save(transactionCaptor.capture());
		com.iolivares.codeChallenge.bank.model.repository.Transaction createdTransaction = transactionCaptor.getValue();
		assertThat(createdTransaction.getReference()).isNotNull();
		assertEquals(createdTransaction.getAccountIban(), "ES9820385778983000760236");
		assertEquals(createdTransaction.getDate(), DateUtils.StringDateToLong("2019-07-16T16:55:42.000Z"), 0.01);
		assertEquals(createdTransaction.getAmount(), 193.38, 0.01);
		assertEquals(createdTransaction.getFee(), 3.18, 0.01);
		assertEquals(createdTransaction.getDescription(), "Restaurant payment");
	}
	
	@Test
	public void testCreateTransactionNoDateOk() {

		// Given
		Account mockedAccount = new Account();
		mockedAccount.setBalance(5000.0);
		mockedAccount.setIban("ES9820385778983000760236");
		mockedAccount.setHolder("Ignacio");

		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");

		// When
		when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
		when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());
		transactionService.createTransaction(newTransaction);

		// Then
		ArgumentCaptor<com.iolivares.codeChallenge.bank.model.repository.Transaction> transactionCaptor = ArgumentCaptor.forClass(com.iolivares.codeChallenge.bank.model.repository.Transaction.class);
		verify(transactionRepository).save(transactionCaptor.capture());
		com.iolivares.codeChallenge.bank.model.repository.Transaction createdTransaction = transactionCaptor.getValue();
		assertThat(createdTransaction.getReference()).isNotNull();
		assertEquals(createdTransaction.getAccountIban(), "ES9820385778983000760236");
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

		// Then
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

			// When
			when(transactionValidator.validate(newTransaction))
					.thenReturn(Arrays.asList("The Amount is required"));
			transactionService.createTransaction(newTransaction);

		});

		// Then
		assertTrue(exception.getMessage().contains("Create Transaction validation error"));
	}
	
	@Test
	public void testCreateTransactionBalanceValidationError() {

		// Expected exception
		TechnicalException exception = Assertions.assertThrows(TechnicalException.class, () -> {

			// Given
			Account mockedAccount = new Account();
			mockedAccount.setBalance(1000.0);
			mockedAccount.setIban("ES9820385778983000760236");
			mockedAccount.setHolder("Ignacio");

			CreateTransactionCommand newTransaction = new CreateTransactionCommand();
			newTransaction.setReference("12345A");
			newTransaction.setAccount_iban("ES9820385778983000760236");
			newTransaction.setDate("2019-07-16T16:55:42.000Z");
			newTransaction.setAmount(-1193.38);
			newTransaction.setFee(3.18);
			newTransaction.setDescription("Restaurant payment");
			
			// When
			when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
			when(transactionValidator.validateAccountBalance(newTransaction,1000.0 ))
					.thenReturn(Arrays.asList("A transaction can't leaves the total account balance bellow 0"));
			transactionService.createTransaction(newTransaction);

		});

		// Then
		assertTrue(exception.getMessage().contains("Create Transaction balance validation error"));
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
			com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();

			CreateTransactionCommand newTransaction = new CreateTransactionCommand();
			newTransaction.setReference("12345A");
			newTransaction.setAccount_iban("ES9820385778983000760236");
			newTransaction.setDate("2019-07-16T16:55:42.000Z");
			newTransaction.setAmount(193.38);
			newTransaction.setFee(3.18);
			newTransaction.setDescription("Restaurant payment");

			// When
			when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
			when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
			transactionService.createTransaction(newTransaction);
		});

		// Then
		assertTrue(exception.getMessage().contains("Create Transaction reference already exist"));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSearchTransaction() {
		
		// Given
		List<com.iolivares.codeChallenge.bank.model.repository.Transaction> mockedTransactions = factory.manufacturePojoWithFullData(ArrayList.class, com.iolivares.codeChallenge.bank.model.repository.Transaction.class);
		
		// When
		when(transactionRepository.findByAccountIban(any(), any())).thenReturn(mockedTransactions);
		List<Transaction> response = transactionService.searchTransactions("12345A", "ASC");
		
		// Then
		assertThat(response).isNotNull();
		assertThat(response).isNotEmpty();
		
	}
	
	@Test
	public void testTransactionStatusCaseA() {
		
		// Given
		String reference = "XXXXXX";
		String channel = "CLIENT";

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "INVALID");
	}
	
	@Test
	public void testTransactionStatusCaseB_ChannelClient() {
		// Given
		String reference = "12345A";
		String channel = "CLIENT";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //yesterday on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "SETTLED");
		assertEquals(transactionStatus.getAmount(), (mockedTransaction.getAmount()-mockedTransaction.getFee()), 0.01);
	}
	
	@Test
	public void testTransactionStatusCaseB_ChannelATM() {
		// Given
		String reference = "12345A";
		String channel = "ATM";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //yesterday on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "SETTLED");
		assertEquals(transactionStatus.getAmount(), (mockedTransaction.getAmount()-mockedTransaction.getFee()), 0.01);
	}
	
	@Test
	public void testTransactionStatusCaseC() {
		// Given
		String reference = "12345A";
		String channel = "INTERNAL";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //yesterday on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "SETTLED");
		assertEquals(transactionStatus.getAmount(), mockedTransaction.getAmount(), 0.01);
		assertEquals(transactionStatus.getFee(),mockedTransaction.getFee(),0.01);
	}
	
	@Test
	public void testTransactionStatusCaseD_ChannelClient() {
		// Given
		String reference = "12345A";
		String channel = "CLIENT";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //today on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "PENDING");
		assertEquals(transactionStatus.getAmount(), (mockedTransaction.getAmount()-mockedTransaction.getFee()), 0.01);
	}
	
	@Test
	public void testTransactionStatusCaseD_ChannelATM() {
		// Given
		String reference = "12345A";
		String channel = "ATM";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //today on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "PENDING");
		assertEquals(transactionStatus.getAmount(), (mockedTransaction.getAmount()-mockedTransaction.getFee()), 0.01);
	}
	
	@Test
	public void testTransactionStatusCaseE() {
		// Given
		String reference = "12345A";
		String channel = "INTERNAL";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //today on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "PENDING");
		assertEquals(transactionStatus.getAmount(), mockedTransaction.getAmount(), 0.01);
		assertEquals(transactionStatus.getFee(),mockedTransaction.getFee(),0.01);
	}
	
	@Test
	public void testTransactionStatusCaseF() {
		// Given
		String reference = "12345A";
		String channel = "CLIENT";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().plusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //tomorrow on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "FUTURE");
		assertEquals(transactionStatus.getAmount(), (mockedTransaction.getAmount()-mockedTransaction.getFee()), 0.01);
	}
	
	@Test
	public void testTransactionStatusCaseG() {
		// Given
		String reference = "12345A";
		String channel = "ATM";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().plusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //tomorrow on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "PENDING");
		assertEquals(transactionStatus.getAmount(), (mockedTransaction.getAmount()-mockedTransaction.getFee()), 0.01);
	}
	
	@Test
	public void testTransactionStatusCaseH() {
		// Given
		String reference = "12345A";
		String channel = "INTERNAL";
		com.iolivares.codeChallenge.bank.model.repository.Transaction mockedTransaction = new com.iolivares.codeChallenge.bank.model.repository.Transaction();
		mockedTransaction.setReference("12345A");
		mockedTransaction.setAccountIban("ES9820385778983000760236");
		mockedTransaction.setAmount(193.38);
		mockedTransaction.setDate(LocalDate.now().plusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //tomorrow on long
		mockedTransaction.setFee(3.18);
		mockedTransaction.setDescription("Restaurant payment");

		// When
		when(transactionRepository.findById(anyString())).thenReturn(Optional.of(mockedTransaction));
		TransactionStatus transactionStatus = transactionService.searchTransactionStatus(reference, channel);
		
		//Then
		assertEquals(transactionStatus.getReference(), reference);
		assertEquals(transactionStatus.getStatus(), "FUTURE");
		assertEquals(transactionStatus.getAmount(), mockedTransaction.getAmount(), 0.01);
		assertEquals(transactionStatus.getFee(),mockedTransaction.getFee(),0.01);
	}

}

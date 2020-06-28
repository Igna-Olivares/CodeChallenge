package com.iolivares.codeChallenge.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() {
		transactionService.setDefaultMapper(orikaConfiguration.defaultMapper());
	}

	@Test
	public void testCreateOk() {

		// Given
		Account mockedAccount = new Account();
		mockedAccount.setBalance(5000.0);
		mockedAccount.setIban("ES9820385778983000760236");
		mockedAccount.setHolder("Ignacio");

		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setReference("12345A");
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");

		// When
		when(accountRepository.findByIban(anyString())).thenReturn(mockedAccount);
		transactionService.createTransaction(newTransaction);

		// Then
		ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionRepository).save(transactionCaptor.capture());
		Transaction createdTransaction = transactionCaptor.getValue();
		assertThat(createdTransaction.getId()).isNotNull();
		assertEquals(createdTransaction.getReference(), "12345A");
		assertEquals(createdTransaction.getAccount_iban(), "ES9820385778983000760236");
		assertEquals(createdTransaction.getReference(), "12345A");
		assertEquals(createdTransaction.getDate(), DateUtils.StringDateToLong("2019-07-16T16:55:42.000Z"));
		assertEquals(createdTransaction.getAmount(), 193.38, 0.01);
		assertEquals(createdTransaction.getFee(), 3.18, 0.01);
		assertEquals(createdTransaction.getDescription(), "Restaurant payment");
	}

	@Test
	public void testCreateNotExistingAccount() {

		// Expected exception
		expectedEx.expect(TechnicalException.class);
		expectedEx.expectMessage("There is no account associated with that IBAN");

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
	}

	@Test
	public void testCreateValidationError() {

		// Expected exception
		expectedEx.expect(TechnicalException.class);
		expectedEx.expectMessage("Create Transaction validation error");

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
		when(transactionValidator.validate(newTransaction,5000.0)).thenReturn(Arrays.asList("The Amount is required"));
		transactionService.createTransaction(newTransaction);
	}

}

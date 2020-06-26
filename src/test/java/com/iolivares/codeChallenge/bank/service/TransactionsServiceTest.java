package com.iolivares.codeChallenge.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.iolivares.codeChallenge.CodeChallengeApplication;
import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.repository.Transaction;
import com.iolivares.codeChallenge.bank.repository.TransactionRepository;
import com.iolivares.codeChallenge.bank.service.impl.TransactionServiceImpl;
import com.iolivares.codeChallenge.common.configuration.OrikaConfiguration;
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
	
	@Before
	public void setUp() {
		transactionService.setDefaultMapper(orikaConfiguration.defaultMapper());
	}
	
	@Test
	public void testCreateOk() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setReference("12345A");
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
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
		
	}
	
	@Test
	public void testCreateValidationError() {
	
	}
	

}

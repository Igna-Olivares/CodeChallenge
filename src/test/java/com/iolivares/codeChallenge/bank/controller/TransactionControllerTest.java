package com.iolivares.codeChallenge.bank.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.iolivares.codeChallenge.bank.enumerations.TransactionChannels;
import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.api.Transaction;
import com.iolivares.codeChallenge.bank.model.api.TransactionStatus;
import com.iolivares.codeChallenge.bank.service.impl.TransactionServiceImpl;
import com.iolivares.codeChallenge.common.configuration.OrikaConfiguration;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerTest {

	private static final PodamFactory factory = new PodamFactoryImpl();

	private OrikaConfiguration orikaConfiguration = new OrikaConfiguration();

	@InjectMocks
	private TransactionController transactionController;

	@Mock
	private TransactionServiceImpl transactionService;

	@Before
	public void setUp() {
		transactionController.setDefaultMapper(orikaConfiguration.defaultMapper());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTransactionsTest() {

		// Given
		List<com.iolivares.codeChallenge.bank.model.service.Transaction> mockedTransactionList = factory
				.manufacturePojo(ArrayList.class, com.iolivares.codeChallenge.bank.model.service.Transaction.class);

		// When
		when(transactionService.searchTransactions(anyString(), anyString())).thenReturn(mockedTransactionList);
		List<Transaction> response = transactionController.getTransactions("ES9820385778983000760236", "ASC");

		// Then
		assertThat(response).isNotNull();
		assertThat(response).isNotEmpty();

	}

	@Test
	public void getTransactionsStatusTest() {

		// Given
		com.iolivares.codeChallenge.bank.model.service.TransactionStatus mockedTransactionStatus = factory
				.manufacturePojo(com.iolivares.codeChallenge.bank.model.service.TransactionStatus.class);

		// When
		when(transactionService.searchTransactionStatus(anyString(), anyString())).thenReturn(mockedTransactionStatus);
		TransactionStatus response = transactionController.getTransactionsStatus("12345A", TransactionChannels.ATM);

		// Then
		assertThat(response).isNotNull();

	}
	
	@Test
	public void createTransactionTest() {

		// Given
		com.iolivares.codeChallenge.bank.model.service.Transaction mockedTransactionStatus = factory
				.manufacturePojo(com.iolivares.codeChallenge.bank.model.service.Transaction.class);
		CreateTransactionCommand command = new CreateTransactionCommand();

		// When
		when(transactionService.createTransaction(command)).thenReturn(mockedTransactionStatus);
		Transaction response = transactionController.createTransaction(command);

		// Then
		assertThat(response).isNotNull();

	}
}

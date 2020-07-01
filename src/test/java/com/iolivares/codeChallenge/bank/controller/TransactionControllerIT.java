package com.iolivares.codeChallenge.bank.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.api.Transaction;
import com.iolivares.codeChallenge.bank.model.api.TransactionStatus;
import com.iolivares.codeChallenge.bank.service.impl.TransactionServiceImpl;
import com.iolivares.codeChallenge.common.configuration.OrikaConfiguration;

import ma.glasnost.orika.MapperFacade;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TransactionControllerIT {
	
	private static final PodamFactory factory = new PodamFactoryImpl();

	private OrikaConfiguration orikaConfiguration = new OrikaConfiguration();

	private MapperFacade defaultMapper;

	@MockBean
	private TransactionServiceImpl transactionService;

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setUp() {
		defaultMapper = orikaConfiguration.defaultMapper();
	}

	@Test
	public void createTransaction() {

		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setReference("12345X");
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		com.iolivares.codeChallenge.bank.model.service.Transaction mockedTransaction = defaultMapper.map(newTransaction,
				com.iolivares.codeChallenge.bank.model.service.Transaction.class);
		
		// When
		when(transactionService.createTransaction(any())).thenReturn(mockedTransaction);
		ResponseEntity<Transaction> result = restTemplate.postForEntity("http://localhost:8080/api/v1/transactions-manager", newTransaction,
				Transaction.class);

		// Then
		assertEquals(result.getStatusCode(), HttpStatus.OK);
		Transaction response = result.getBody();
		assertThat(response.getReference()).isNotNull();
		assertEquals(response.getAccount_iban(), "ES9820385778983000760236");
		assertEquals(response.getDate(), "2019-07-16T16:55:42.000Z");
		assertEquals(response.getAmount(), 193.38, 0.01);
		assertEquals(response.getFee(), 3.18, 0.01);
		assertEquals(response.getDescription(), "Restaurant payment");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void searchTransaction() {

		// Given
		List<com.iolivares.codeChallenge.bank.model.service.Transaction> mockedTransactionList = factory
				.manufacturePojo(ArrayList.class, com.iolivares.codeChallenge.bank.model.service.Transaction.class);

		// When
		when(transactionService.searchTransactions(any(), any())).thenReturn(mockedTransactionList);
		ResponseEntity<Transaction[]> result = restTemplate.getForEntity("http://localhost:8080/api/v1/transactions-manager",
				Transaction[].class, "ES9820385778983000760236", Direction.ASC);

		// Then
		assertEquals(result.getStatusCode(), HttpStatus.OK);
		List<Object> response = Arrays.asList(result.getBody());
		assertThat(response).isNotNull();
		assertThat(response).isNotEmpty();
	}

	@Test
	public void searchTransactionStatus() {
		// Given
		com.iolivares.codeChallenge.bank.model.service.TransactionStatus expectedTransactionStatus = new com.iolivares.codeChallenge.bank.model.service.TransactionStatus();
		expectedTransactionStatus.setReference("12345B");
		expectedTransactionStatus.setStatus("SETTLED");
		expectedTransactionStatus.setAmount(452.3);
		expectedTransactionStatus.setFee(3.18);

	

		// When
		when(transactionService.searchTransactionStatus(any(), any())).thenReturn(expectedTransactionStatus);
		ResponseEntity<TransactionStatus> result = restTemplate.getForEntity(
				"http://localhost:8080/api/v1/transactions-manager/transaction-status?channel=INTERNAL&reference=12345B", TransactionStatus.class);

		// Then
		assertEquals(result.getStatusCode(), HttpStatus.OK);
		TransactionStatus response = result.getBody();
		assertThat(response).isNotNull();
		assertEquals(response.getReference(), expectedTransactionStatus.getReference());
		assertEquals(response.getStatus(), expectedTransactionStatus.getStatus());
		assertEquals(response.getAmount(), expectedTransactionStatus.getAmount(), 0.01);
		assertEquals(response.getFee(),expectedTransactionStatus.getFee(),0.01);
	}
}
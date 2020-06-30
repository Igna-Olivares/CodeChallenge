package com.iolivares.codeChallenge.bank.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.iolivares.codeChallenge.bank.model.repository.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionRepositoryIT {

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private TransactionRepository transactionRepository;

	@Test
	public void tryToSaveTransactionAndFindIt() {
		// Given
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		Transaction newTransaction = new Transaction();
		newTransaction.setReference("12345A");
		newTransaction.setAccountIban("ES9820385778983000760236");
		newTransaction.setAmount(193.38);
		newTransaction.setDate(LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); //yesterday on long
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		// When
		transactionTemplate.execute(s -> transactionRepository.save(newTransaction));
		Optional<Transaction> resultOpcional = transactionTemplate.execute(s -> transactionRepository.findById("12345A"));

		// Then
		Transaction result = resultOpcional.orElse(null);
		assertThat(result).isNotNull();
		assertEquals(result.getReference(), "12345A");
		assertEquals(result.getAccountIban(), "ES9820385778983000760236");
		assertEquals(result.getDate(), LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		assertEquals(result.getAmount(), 193.38, 0.01);
		assertEquals(result.getFee(), 3.18, 0.01);
		assertEquals(result.getDescription(), "Restaurant payment");
	}
	
	@Test
	public void tryToFindNoFiltering() {
		
		// Given
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		
		// When
		List<Transaction> result = transactionTemplate.execute(s -> transactionRepository.findByAccountIban(null,null));

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
	}
	
	@Test
	public void tryToFindFiltering() {
		
		// Given
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		
		// When
		List<Transaction> result = transactionTemplate.execute(s -> transactionRepository.findByAccountIban("ES9820385778983000760236",null));

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertEquals(4, result.size());
	}
	
	@Test
	public void tryToFindSortingDesc() {
		
		// Given
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		
		// When
		List<Transaction> result = transactionTemplate.execute(s -> transactionRepository.findByAccountIban(null,Sort.by(Direction.DESC, "amount")));

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertEquals("12345E", result.get(0).getReference());
		assertEquals("12345A", result.get(result.size()-1).getReference());
		
	}
	
	@Test
	public void tryToFindFilteringSortingDesc() {
		
		// Given
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		
		// When
		List<Transaction> result = transactionTemplate.execute(s -> transactionRepository.findByAccountIban("ES9820385778983000760236",Sort.by(Direction.DESC, "amount")));

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertEquals(4, result.size());
		assertEquals("12345D", result.get(0).getReference());
		assertEquals("12345A", result.get(result.size()-1).getReference());
		
	}
}
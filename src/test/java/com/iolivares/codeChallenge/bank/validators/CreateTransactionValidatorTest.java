package com.iolivares.codeChallenge.bank.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.validators.impl.CreateTransactionValidatorImpl;

@RunWith(MockitoJUnitRunner.class)
public class CreateTransactionValidatorTest {

	@InjectMocks
	private CreateTransactionValidatorImpl validator;
	
	@Test
	public void testValidateOk() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
		List<String> errors = validator.validate(newTransaction);
		
		// Then
		assertThat(errors).isNotNull();
		assertThat(errors).isEmpty();
	}
	
	@Test
	public void testValidateIbanError() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
		List<String> errors = validator.validate(newTransaction);
		
		// Then
		assertThat(errors).isNotNull();
		assertThat(errors).isNotEmpty();
		assertEquals("The account iban is required", errors.get(0));
	}
	
	@Test
	public void testValidateAmountError() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
		List<String> errors = validator.validate(newTransaction);
		
		// Then
		assertThat(errors).isNotNull();
		assertThat(errors).isNotEmpty();
		assertEquals("The Amount is required", errors.get(0));
	}
	
	@Test
	public void testValidateAccountBalanceOk() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
		List<String> errors = validator.validateAccountBalance(newTransaction, 1000.0);
		
		// Then
		assertThat(errors).isNotNull();
		assertThat(errors).isEmpty();
	}
	
	@Test
	public void testValidateAccountBalanceErrorOnAmount() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(-193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
		List<String> errors = validator.validateAccountBalance(newTransaction, 100.0);
		
		// Then
		assertThat(errors).isNotNull();
		assertThat(errors).isNotEmpty();
		assertEquals("A transaction can't leaves the total account balance bellow 0", errors.get(0));
	}
	
	@Test
	public void testValidateAccountBalanceErrorOnFee() {
		
		// Given
		CreateTransactionCommand newTransaction = new CreateTransactionCommand();
		newTransaction.setAccount_iban("ES9820385778983000760236");
		newTransaction.setDate("2019-07-16T16:55:42.000Z");
		newTransaction.setAmount(-193.38);
		newTransaction.setFee(3.18);
		newTransaction.setDescription("Restaurant payment");
		
		// When
		List<String> errors = validator.validateAccountBalance(newTransaction, 194.38);
		
		// Then
		assertThat(errors).isNotNull();
		assertThat(errors).isNotEmpty();
		assertEquals("A transaction minus the fee can't leaves the total account balance bellow 0", errors.get(0));
	}
}

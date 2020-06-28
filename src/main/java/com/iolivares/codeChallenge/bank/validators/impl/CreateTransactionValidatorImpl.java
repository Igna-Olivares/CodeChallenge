package com.iolivares.codeChallenge.bank.validators.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.validators.CreateTransactionValidator;

@Component
public class CreateTransactionValidatorImpl implements CreateTransactionValidator {

	@Override
	public List<String> validate(CreateTransactionCommand newTransaction, Double accountBalance) {
		
		List<String> errorList = new ArrayList<>();
		
		if(StringUtils.isEmpty(newTransaction.getAccount_iban())) {
			errorList.add("The account iban is required");
		}
		
		if(newTransaction.getAmount() == null) {
			errorList.add("The Amount is required");
		}
		
		if(newTransaction.getAmount() != null && (accountBalance + newTransaction.getAmount() == 0) ) {
			errorList.add("A transaction can't leaves the total account balance bellow 0");
		}
		
		if(newTransaction.getFee() != null && (accountBalance - newTransaction.getFee() == 0) ) {
			errorList.add("A transaction fee can't leaves the total account balance bellow 0");
		}
			
		return errorList;
	}

}
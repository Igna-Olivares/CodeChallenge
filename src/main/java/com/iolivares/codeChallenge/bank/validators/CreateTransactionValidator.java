package com.iolivares.codeChallenge.bank.validators;

import java.util.List;

import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;

public interface CreateTransactionValidator {

	public List<String> validate(CreateTransactionCommand newTransaction, Double accountBalance);
}

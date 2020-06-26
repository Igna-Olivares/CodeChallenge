package com.iolivares.codeChallenge.bank.model.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionCommand {

	private String reference;
	
	private String account_iban;
	
	private String date;
	
	private double amount;
	
	private double fee;
	
	private String description;
}

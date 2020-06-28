package com.iolivares.codeChallenge.bank.model.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionCommand {

	private String reference;
	
	private String account_iban;
	
	private String date;
	
	private Double amount;
	
	private Double fee;
	
	private String description;
}

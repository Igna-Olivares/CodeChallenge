package com.iolivares.codeChallenge.bank.model.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStatus {

	private String reference;
	
	private String status;
	
	private Double amount;
	
	private Double fee;
}

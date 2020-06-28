package com.iolivares.codeChallenge.bank.model.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

	private String reference;

	private String account_iban;

	private long date;

	private Double amount;

	private Double fee;

	private String description;
}

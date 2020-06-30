package com.iolivares.codeChallenge.bank.model.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Transaction {

	@Id
	@Column(length = 6)
	private String reference;

	@Column(name = "account_iban")
	private String accountIban;

	private Long date;

	private Double amount;

	private Double fee;

	private String description;
}

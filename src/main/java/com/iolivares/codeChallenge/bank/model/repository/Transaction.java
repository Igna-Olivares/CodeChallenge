package com.iolivares.codeChallenge.bank.model.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

	@Id
	@GeneratedValue
	private int id;
	
	@Column(unique=true)
	private String reference;
	
	private String account_iban;
	
	private long date;
	
	private double amount;
	
	private double fee;
	
	private String description;
}

package com.iolivares.codeChallenge.bank.model.service;

import com.iolivares.codeChallenge.bank.enumerations.TransactionStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStatus {

	private String reference;
	
	private String status;
	
	private Double amount;
	
	private Double fee;
	
	public void setInvalidStatus() {
		setStatus(TransactionStatusEnum.INVALID.getCode());
	}
	
	public void setSettledStatus() {
		setStatus(TransactionStatusEnum.SETTLED.getCode());
	}
	
	public void setPendingStatus() {
		setStatus(TransactionStatusEnum.PENDING.getCode());
	}
	
	public void setFutureStatus() {
		setStatus(TransactionStatusEnum.FUTURE.getCode());
	}
}

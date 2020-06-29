package com.iolivares.codeChallenge.bank.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionStatusEnum {

	INVALID("INVALID"), SETTLED("SETTLED"), PENDING("PENDING"), FUTURE("FUTURE");

	private String code;
}

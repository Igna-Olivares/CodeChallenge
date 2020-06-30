package com.iolivares.codeChallenge.bank.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionChannels {

	CLIENT("CLIENT"), ATM("ATM"), INTERNAL("INTERNAL");

	private String code;
}

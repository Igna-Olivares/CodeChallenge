package com.iolivares.codeChallenge.bank.enumerations;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApiModel("The type of the channel that is asking for the status. It can be any of these values: CLIENT, ATM, INTERNAL")
@Getter
@AllArgsConstructor
public enum TransactionChannels {

	CLIENT("CLIENT"), ATM("ATM"), INTERNAL("INTERNAL");

	private String code;
}

package com.iolivares.codeChallenge.bank.model.api;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApiModel(description = "Sentido de ordenación en consultas")
@Getter
@AllArgsConstructor
public enum AmountDirectionEnum {

	ASC("ASC", "Ascending"), DESC("DESC", "Descending");

	private String code;
	private String description;

}

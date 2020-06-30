package com.iolivares.codeChallenge.common.model.error;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorItem {

	private int code;
	private String message;
	private List<String> errors;
}

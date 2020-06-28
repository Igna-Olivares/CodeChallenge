package com.iolivares.codeChallenge.common.exceptions;

import java.util.List;

import lombok.Getter;

public class TechnicalException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7198556807883254762L;
	
	@Getter
	private final int code;
	
	@Getter
	private final List<String> errors;

	public TechnicalException(String errorMsg, int errorCode) {
		super(errorMsg);
		this.code = errorCode;
		this.errors = null;
	}

	public TechnicalException(String errorMsg, int errorCode, List<String> errorList) {
		super(errorMsg);
		this.code = errorCode;
		this.errors = errorList;
	}
}

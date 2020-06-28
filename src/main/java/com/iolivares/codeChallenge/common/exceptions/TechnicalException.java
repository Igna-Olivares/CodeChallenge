package com.iolivares.codeChallenge.common.exceptions;

import lombok.Getter;

public class TechnicalException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7198556807883254762L;
	
	@Getter
	private final int code;

	public TechnicalException(String errorMsg, int errorCode) {
		super(errorMsg);
		this.code = errorCode;
	}
}

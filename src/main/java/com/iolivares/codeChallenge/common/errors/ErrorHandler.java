package com.iolivares.codeChallenge.common.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.iolivares.codeChallenge.common.exceptions.TechnicalException;
import com.iolivares.codeChallenge.common.model.error.ErrorItem;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(TechnicalException.class)
	public ResponseEntity<ErrorItem> handleTechnicalException(TechnicalException ex) {
		ErrorItem errorItem = new ErrorItem();
		errorItem.setCode(ex.getCode());
		errorItem.setMessage(ex.getMessage());
		errorItem.setErrors(ex.getErrors());
		return new ResponseEntity<ErrorItem>(errorItem, HttpStatus.valueOf(ex.getCode()));
	}

}
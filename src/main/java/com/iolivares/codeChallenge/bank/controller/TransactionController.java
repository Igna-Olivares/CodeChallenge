package com.iolivares.codeChallenge.bank.controller;

import static com.iolivares.codeChallenge.bank.controller.TransactionController.API_VERSION;
import static com.iolivares.codeChallenge.bank.controller.TransactionController.TRANSACTIONS_PATH;
import static com.iolivares.codeChallenge.bank.controller.TransactionController.TRANSACTIONS_TAG;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iolivares.codeChallenge.bank.enumerations.TransactionChannels;
import com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand;
import com.iolivares.codeChallenge.bank.model.api.Transaction;
import com.iolivares.codeChallenge.bank.model.api.TransactionStatus;
import com.iolivares.codeChallenge.bank.service.TransactionService;
import com.iolivares.codeChallenge.common.configuration.SwaggerConfiguration;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Setter;
import ma.glasnost.orika.MapperFacade;

@RestController
@RequestMapping(value = SwaggerConfiguration.API_BASE_PATH + API_VERSION + TRANSACTIONS_PATH)
@Api(tags = { TRANSACTIONS_TAG })
public class TransactionController {
	
	public static final String API_VERSION = "/v1";
	public static final String TRANSACTIONS_PATH = "/transactions-manager";
	public static final String TRANSACTIONS_TAG = "Transactions Manager";
	
	@Setter
	@Autowired
	private MapperFacade defaultMapper;
	
	@Autowired
	private TransactionService transactionService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "")
	@ApiOperation(value = "Get transactions filtered")
	public List<Transaction> getTransactions( @RequestParam(required = false) String account_iban, @ApiParam("Permited values ASC/DESC") @RequestParam(required = false) String direction) {

		return defaultMapper.mapAsList(transactionService.searchTransactions(account_iban, direction), Transaction.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "/transaction-status")
	@ApiOperation(value = "Get transactions status")
	public TransactionStatus getTransactionsStatus( @RequestParam String reference, @RequestParam TransactionChannels channel) {

		return defaultMapper.map(transactionService.searchTransactionStatus(reference, channel.getCode()), TransactionStatus.class);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, value = "")
	@ApiOperation(value = "Create new Transactions")
	public Transaction createTransaction( @RequestBody CreateTransactionCommand createTransactionCommand) {

		return defaultMapper.map(transactionService.createTransaction(createTransactionCommand), Transaction.class);
	}



}

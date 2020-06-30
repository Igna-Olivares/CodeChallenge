package com.iolivares.codeChallenge.bank.controller;

import static com.iolivares.codeChallenge.bank.controller.TransactionController.API_VERSION;
import static com.iolivares.codeChallenge.bank.controller.TransactionController.TRANSACTIONS_PATH;
import static com.iolivares.codeChallenge.bank.controller.TransactionController.TRANSACTIONS_TAG;

import java.util.List;

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
import ma.glasnost.orika.MapperFacade;

@RestController
@RequestMapping(value = SwaggerConfiguration.API_BASE_PATH + API_VERSION + TRANSACTIONS_PATH)
@Api(tags = { TRANSACTIONS_TAG })
public class TransactionController {
	
	public static final String API_VERSION = "/v1";
	public static final String TRANSACTIONS_PATH = "/transactions-manager";
	public static final String TRANSACTIONS_TAG = "Transactions Manager";
	
	private final MapperFacade defaultMapper;
	
	private final TransactionService transactionService;
	
	public TransactionController(MapperFacade defaultMapper, TransactionService transactionService) {
		this.defaultMapper = defaultMapper;
		this.transactionService = transactionService;
	}
	
	/**
	 * Get transactions filtered
	 * 
	 * @param account_iban The IBAN number of the account where the transaction has happened
	 * @param direction Amount Ordering direction
	 * @return List<Transaction> - list of transactions
	 */
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "")
	@ApiOperation(value = "Get transactions filtered")
	public List<Transaction> getTransactions(@ApiParam("The IBAN number of the account where the transaction has happened") @RequestParam(required = false) String account_iban, @ApiParam("Amount Ordering direction. It can be any of these values:ASC,DESC") @RequestParam(required = false) String direction) {

		return defaultMapper.mapAsList(transactionService.searchTransactions(account_iban, direction), Transaction.class);
	}
	
	
	/**
	 * Get the transactions status
	 * 
	 * @param reference The transaction reference number
	 * @param channel Amount The type of the channel that is asking for the status direction
	 * @return TransactionStatus - the transaction status detail info
	 */
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "/transaction-status")
	@ApiOperation(value = "Get transactions status")
	public TransactionStatus getTransactionsStatus(
			@ApiParam("The transaction reference number") @RequestParam String reference, @ApiParam("The type of the channel that is asking for the status. It can be any of these values: CLIENT, ATM, INTERNAL") @RequestParam(required = false) TransactionChannels channel) {

		return defaultMapper.map(transactionService.searchTransactionStatus(reference, channel), TransactionStatus.class);
	}
	
	/**
	 * Create new Transactions
	 * 
	 * @param CreateTransactionCommand The transaction objecto with all his parameters
	 * @return Transaction - the new transaction object
	 */
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, value = "")
	@ApiOperation(value = "Create new Transactions")
	public Transaction createTransaction( @RequestBody CreateTransactionCommand createTransactionCommand) {

		return defaultMapper.map(transactionService.createTransaction(createTransactionCommand), Transaction.class);
	}



}

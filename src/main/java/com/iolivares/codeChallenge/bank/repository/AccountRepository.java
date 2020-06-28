package com.iolivares.codeChallenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iolivares.codeChallenge.bank.model.repository.Account;

public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	Account findByIban(String iban);

}

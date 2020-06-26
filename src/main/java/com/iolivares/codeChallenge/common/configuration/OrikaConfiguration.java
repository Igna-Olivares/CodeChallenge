package com.iolivares.codeChallenge.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.iolivares.codeChallenge.common.utils.DateUtils;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Configuration
public class OrikaConfiguration {

	@Bean
	public MapperFacade defaultMapper() {

		MapperFactory factory = new DefaultMapperFactory.Builder().build();

		/** Custom mappings **/
		factory.classMap(com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand.class,
				com.iolivares.codeChallenge.bank.model.repository.Transaction.class). //
				exclude("date").
				byDefault(). //
				customize(
						new CustomMapper<com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand, com.iolivares.codeChallenge.bank.model.repository.Transaction>() {
							@Override
							public void mapAtoB(com.iolivares.codeChallenge.bank.model.api.CreateTransactionCommand a,
									com.iolivares.codeChallenge.bank.model.repository.Transaction b,
									MappingContext context) {
								super.mapAtoB(a, b, context);
								b.setDate(DateUtils.StringDateToLong(a.getDate()));
							}
						})
				. //
				register();


		return factory.getMapperFacade();

	}
}

package com.iolivares.codeChallenge.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

	@Test
	public void StringDateToLongTest() {

		// Given
		String inputDate = "2019-07-16T16:55:42.000Z";

		// When
		Long date = DateUtils.StringDateToLong(inputDate);

		// Then
		assertEquals(1563288942000L, date, 0.01);
	}

	@Test
	public void LongDateToStringTest() {

		// Given
		Long inputDate = 1593429998000L;

		// When
		String date = DateUtils.LongDateToString(inputDate);

		// Then
		assertEquals("2020-06-29T13:26:38.000Z", date);
	}

	@Test
	public void compareDatesBeforeToday() {

		// Given
		Long inputDate = LocalDate.now().minusDays(1L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// When
 		int dateDifference = DateUtils.compareDatesToNow(inputDate);

		// Then
		assertTrue(dateDifference == -1);
	}
	
	@Test
	public void compareDatesEqualsToday() {

		// Given
		Long inputDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// When
 		int dateDifference = DateUtils.compareDatesToNow(inputDate);

		// Then
		assertTrue(dateDifference == 0);
	}
	
	@Test
	public void compareDatesAfterToday() {

		// Given
		Long inputDate = LocalDate.now().plusDays(15L).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// When
 		int dateDifference = DateUtils.compareDatesToNow(inputDate);

		// Then
		assertTrue(dateDifference == 1);
	}

}

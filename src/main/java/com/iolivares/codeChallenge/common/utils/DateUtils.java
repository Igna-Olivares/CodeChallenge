package com.iolivares.codeChallenge.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static long StringDateToLong(String inputDate) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDateTime date = LocalDateTime.parse(inputDate, inputFormatter);
		return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static String LongDateToString(long inputDate) {
		LocalDateTime date = Instant.ofEpochMilli(inputDate).atZone(ZoneId.systemDefault()).toLocalDateTime();
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return date.format(outputFormatter);
	}

	public static int compareDatesToNow(long inputDate) {
		LocalDate date = Instant.ofEpochMilli(inputDate).atZone(ZoneId.systemDefault()).toLocalDate();
		return date.compareTo(LocalDate.now());
	}
}

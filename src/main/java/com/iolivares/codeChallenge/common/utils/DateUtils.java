package com.iolivares.codeChallenge.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static long StringDateToLong(String inputDate) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDateTime date = LocalDateTime.parse(inputDate, inputFormatter);
		return date.atZone(ZoneId.systemDefault()).toEpochSecond();
	}
	
	public static String LongDateToString(long inputDate) {
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(inputDate), ZoneId.systemDefault());
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return date.format(outputFormatter);
	}
}

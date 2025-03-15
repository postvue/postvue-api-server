package com.postvue.feelogserver.global.util.generator;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public final class DateUtils {
	public static LocalDateTime getRandomDateTime(LocalDateTime start, LocalDateTime end) {
		long startEpoch = start.toEpochSecond(java.time.ZoneOffset.UTC);
		long endEpoch = end.toEpochSecond(java.time.ZoneOffset.UTC);

		long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
		return LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);
	}

}

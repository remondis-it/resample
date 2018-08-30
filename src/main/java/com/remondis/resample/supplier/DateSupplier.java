package com.remondis.resample.supplier;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import com.remondis.resample.FieldInfo;

/**
 * Supplies {@link ZonedDateTime} instances.
 */
public class DateSupplier {

	/**
	 * @return Returns a period supplier that generates {@link ZonedDateTime}s:
	 *         <ul>
	 *         <li>If the field name contains the word 'from' yesterday is returned
	 *         as {@link ZonedDateTime}.</li>
	 *         <li>If the field name contains the word 'to' tomorrow is returned as
	 *         {@link ZonedDateTime}.</li>
	 *         <li>For all other field names the generation time is returned.</li>
	 */
	public static Function<FieldInfo, LocalDate> localDateSupplier() {
		return (info) -> {
			if (info.getPropertyName().contains("from")) {
				return LocalDate.now().minus(1, ChronoUnit.DAYS);
			} else if (info.getPropertyName().contains("to")) {
				return LocalDate.now().plus(1, ChronoUnit.DAYS);
			} else {
				return LocalDate.now();
			}
		};
	}

	/**
	 * @return Returns a period supplier that generates {@link ZonedDateTime}s:
	 *         <ul>
	 *         <li>If the field name contains the word 'from' yesterday is returned
	 *         as {@link ZonedDateTime}.</li>
	 *         <li>If the field name contains the word 'to' tomorrow is returned as
	 *         {@link ZonedDateTime}.</li>
	 *         <li>For all other field names the generation time is returned.</li>
	 */
	public static Function<FieldInfo, ZonedDateTime> zonedDateTimeSupplier() {
		return (info) -> {
			if (info.getPropertyName().toLowerCase().contains("from")) {
				return ZonedDateTime.now().minus(1, ChronoUnit.DAYS);
			} else if (info.getPropertyName().toLowerCase().contains("to")) {
				return ZonedDateTime.now().plus(1, ChronoUnit.DAYS);
			} else {
				return ZonedDateTime.now();
			}
		};
	}

}

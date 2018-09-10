package com.remondis.resample.supplier;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import com.remondis.resample.FieldInfo;

/**
 * A collection of the most common suppliers.
 */
public class Suppliers {

	/**
	 * @return Returns a supplier of a sample enum value.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Function<FieldInfo, T> enumValueSupplier() {
		return (fi) -> {
			return (T) fi.getType().getEnumConstants()[0];
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

	/**
	 * @return Returns a sample supplier returning empty strings.
	 */
	public static Supplier<String> emptyStringSupplier() {
		return () -> {
			return new String();
		};
	}

	/**
	 * @return Returning a sample supplier returning the field name as string.
	 */
	public static Function<FieldInfo, String> fieldNameStringSupplier() {
		return (fi) -> {
			return new String(fi.getPropertyName());
		};
	}
}

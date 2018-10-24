package com.remondis.resample.supplier;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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
      return (T) fi.getType()
          .getEnumConstants()[0];
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
      if (isPeriodStartField(info)) {
        return LocalDate.now()
            .minus(1, ChronoUnit.DAYS);
      } else if (isPeriodEndField(info)) {
        return LocalDate.now()
            .plus(1, ChronoUnit.DAYS);
      } else {
        return LocalDate.now();
      }
    };
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' yesterday is returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' tomorrow is returned as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, Date> dateSupplier() {
    final Calendar cal = Calendar.getInstance();
    return (info) -> {
      if (isPeriodStartField(info)) {
        cal.add(Calendar.DATE, -1);
        return new Date(cal.getTime()
            .getTime());
      } else if (isPeriodEndField(info)) {
        cal.add(Calendar.DATE, 1);
        return new Date(cal.getTime()
            .getTime());
      } else {
        return new Date(cal.getTime()
            .getTime());
      }
    };
  }

  private static boolean isPeriodEndField(FieldInfo info) {
    return containsIngoreCase(info.getPropertyName(), "to") || containsIngoreCase(info.getPropertyName(), "end");
  }

  private static boolean isPeriodStartField(FieldInfo info) {
    return containsIngoreCase(info.getPropertyName(), "from") || containsIngoreCase(info.getPropertyName(), "start");
  }

  static boolean containsIngoreCase(String string, String pattern) {
    requireNonNull(pattern, "pattern");
    requireNonNull(string, "string");
    return string.toLowerCase()
        .contains(pattern.toLowerCase());
  }

  public static Function<FieldInfo, TimeZone> timeZoneSupplier() {
    return fi -> TimeZone.getTimeZone("Europe/Berlin");
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
      if (isPeriodStartField(info)) {
        return ZonedDateTime.now()
            .minus(1, ChronoUnit.DAYS);
      } else if (isPeriodEndField(info)) {
        return ZonedDateTime.now()
            .plus(1, ChronoUnit.DAYS);
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

  public static Function<FieldInfo, Long> defaultLongSupplier() {
    return (fi) -> 0L;
  }

  public static Function<FieldInfo, Boolean> defaultBooleanSupplier() {
    return (fi) -> false;
  }

  public static Function<FieldInfo, Character> defaultCharacterSupplier() {
    return (fi) -> '\0';
  }

  public static Function<FieldInfo, Byte> defaultByteSupplier() {
    return (fi) -> (byte) 0;
  }

  public static Function<FieldInfo, Short> defaultShortSupplier() {
    return (fi) -> (short) 0;
  }

  public static Function<FieldInfo, Integer> defaultIntegerSupplier() {
    return (fi) -> (Integer) 0;
  }

  public static Function<FieldInfo, Float> defaultFloatSupplier() {
    return (fi) -> (Float) 0f;
  }

  public static Function<FieldInfo, Double> defaultDoubleSupplier() {
    return (fi) -> (Double) 0d;
  }

  public static Function<FieldInfo, Long> oneLongSupplier() {
    return (fi) -> 1L;
  }

  public static Function<FieldInfo, Boolean> trueBooleanSupplier() {
    return (fi) -> true;
  }

  public static Function<FieldInfo, Byte> oneByteSupplier() {
    return (fi) -> (byte) 1;
  }

  public static Function<FieldInfo, Short> oneShortSupplier() {
    return (fi) -> (short) 1;
  }

  public static Function<FieldInfo, Integer> oneIntegerSupplier() {
    return (fi) -> (Integer) 1;
  }

  public static Function<FieldInfo, Float> oneFloatSupplier() {
    return (fi) -> (Float) 1f;
  }

  public static Function<FieldInfo, Double> oneDoubleSupplier() {
    return (fi) -> (Double) 1d;
  }

  public static Function<FieldInfo, BigDecimal> defaultBigDecimalSupplier() {
    return (fi) -> BigDecimal.ZERO;
  }

  public static Function<FieldInfo, BigDecimal> oneBigDecimalSupplier() {
    return (fi) -> BigDecimal.ONE;
  }

}
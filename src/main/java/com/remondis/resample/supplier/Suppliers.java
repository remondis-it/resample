package com.remondis.resample.supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Supplier;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.FunctionSupplier;
import com.remondis.resample.SampleSupplier;

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
      Object[] enumConstants = fi.getType()
          .getEnumConstants();
      if (isNull(enumConstants)) {
        throw new IllegalAccessError("Cannot supply enum value from non-enum type: " + fi.getType()
            .getName());
      } else {
        return (T) enumConstants[0];
      }
    };
  }

  /**
   * @param year The year to supply.
   * @param month The month to supply.
   * @param dayOfMonth The day of month to supply.
   * @return Returns a period supplier that generates {@link LocalDate}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link LocalDate}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link LocalDate}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static FunctionSupplier<LocalDate> localDateSampleSupplier(int year, int month, int dayOfMonth) {
    return new FunctionSupplier<>(LocalDate.class, localDateSupplier(year, month, dayOfMonth));
  }

  /**
   * @param year The year to supply.
   * @param month The month to supply.
   * @param dayOfMonth The day of month to supply.
   * @return Returns a period supplier that generates {@link LocalDate}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link LocalDate}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link LocalDate}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, LocalDate> localDateSupplier(int year, int month, int dayOfMonth) {
    return (info) -> {
      if (isPeriodStartField(info)) {
        return LocalDate.of(year, month, dayOfMonth)
            .minus(1, ChronoUnit.DAYS);
      } else if (isPeriodEndField(info)) {
        return LocalDate.of(year, month, dayOfMonth)
            .plus(1, ChronoUnit.DAYS);
      } else {
        return LocalDate.of(year, month, dayOfMonth);
      }
    };
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static FunctionSupplier<Date> dateSampleSupplier(int year, int month, int dayOfMonth, int hourOfDay,
      int minute, int second, int millis) {
    return new FunctionSupplier<>(Date.class, dateSupplier(year, month, dayOfMonth, hourOfDay, minute, second, millis));
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, Date> dateSupplier(int year, int month, int dayOfMonth, int hourOfDay, int minute,
      int second, int millis) {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, second);
    cal.set(Calendar.MILLISECOND, millis);
    return _dateSupplier(cal.getTime());
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'start' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static FunctionSupplier<Date> dateSampleSupplier(int year, int month, int dayOfMonth, int hourOfDay) {
    return new FunctionSupplier<>(Date.class, dateSupplier(year, month, dayOfMonth, hourOfDay));
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, Date> dateSupplier(int year, int month, int dayOfMonth, int hourOfDay) {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return _dateSupplier(cal.getTime());
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static FunctionSupplier<Date> dateSampleSupplier(int year, int month, int dayOfMonth, int hourOfDay,
      int minute) {
    return new FunctionSupplier<>(Date.class, dateSupplier(year, month, dayOfMonth, hourOfDay, minute));
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, Date> dateSupplier(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return _dateSupplier(cal.getTime());
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static FunctionSupplier<Date> dateSampleSupplier(int year, int month, int dayOfMonth, int hourOfDay,
      int minute, int second) {
    return new FunctionSupplier<>(Date.class, dateSupplier(year, month, dayOfMonth, hourOfDay, minute, second));
  }

  /**
   * @return Returns a period supplier that generates {@link Date}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link Date}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link Date}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, Date> dateSupplier(int year, int month, int dayOfMonth, int hourOfDay, int minute,
      int second) {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, second);
    cal.set(Calendar.MILLISECOND, 0);
    return _dateSupplier(cal.getTime());
  }

  private static Function<FieldInfo, Date> _dateSupplier(final Date time) {
    return (info) -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(time);
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

  /**
   * @return Returns a supplier that generates {@link TimeZone}s.
   */
  public static Function<FieldInfo, TimeZone> timeZoneSupplier(String zoneId) {
    return fi -> TimeZone.getTimeZone(zoneId);
  }

  /**
   * @return Returns a supplier that generates {@link TimeZone}s.
   */
  public static SampleSupplier<TimeZone> timeZoneSampleSupplier(String zoneId) {
    return new FunctionSupplier<>(TimeZone.class, timeZoneSupplier(zoneId));
  }

  /**
   * @param zone The {@link ZoneId} to use.
   * @return Returns a period supplier that generates {@link ZonedDateTime}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link ZonedDateTime}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link ZonedDateTime}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static SampleSupplier<ZonedDateTime> zonedDateTimeSampleSupplier(int year, int month, int dayOfMonth, int hour,
      int minute, int second, int nanoOfSecond, ZoneId zone) {
    return new FunctionSupplier<>(ZonedDateTime.class,
        zonedDateTimeSupplier(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone));
  };

  /**
   * @param zone The {@link ZoneId} to use.
   * @return Returns a period supplier that generates {@link ZonedDateTime}s:
   *         <ul>
   *         <li>If the field name contains the word 'from' or 'start' yesterday relative to the specified date is
   *         returned
   *         as {@link ZonedDateTime}.</li>
   *         <li>If the field name contains the word 'to' or 'end' tomorrow relative to the specified date is returned
   *         as
   *         {@link ZonedDateTime}.</li>
   *         <li>For all other field names the generation time is returned.</li>
   */
  public static Function<FieldInfo, ZonedDateTime> zonedDateTimeSupplier(int year, int month, int dayOfMonth, int hour,
      int minute, int second, int nanoOfSecond, ZoneId zone) {
    return (info) -> {
      if (isPeriodStartField(info)) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone)
            .minus(1, ChronoUnit.DAYS);
      } else if (isPeriodEndField(info)) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone)
            .plus(1, ChronoUnit.DAYS);
      } else {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone);
      }
    };
  }

  /**
   * @return Returns a sample supplier returning empty strings.
   */
  public static SampleSupplier<String> emptyStringSampleSupplier() {
    return new FunctionSupplier<>(String.class, (fi) -> emptyStringSupplier().get());
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
   * @return Returns a sample supplier returning empty strings.
   */
  public static SampleSupplier<String> fullyQualifiedFieldNameStringSampleSupplier() {
    return new FunctionSupplier<>(String.class, fullyQualifiedFieldNameStringSupplier());
  }

  /**
   * @return Returning a sample supplier returning the fully-quallified field name as string.
   */
  public static Function<FieldInfo, String> fullyQualifiedFieldNameStringSupplier() {
    return (fi) -> {
      return String.format("%s.%s", fi.getProperty()
          .getReadMethod()
          .getDeclaringClass()
          .getName(), fi.getPropertyName());
    };
  }

  /**
   * @return Returning a sample supplier returning the field name as string with the declaring class' simple name.
   */
  public static SampleSupplier<String> shortQualifiedFieldNameStringSampleSupplier() {
    return new FunctionSupplier<>(String.class, shortQualifiedFieldNameStringSupplier());
  }

  /**
   * @return Returning a sample supplier returning the field name as string with the declaring class' simple name.
   */
  public static Function<FieldInfo, String> shortQualifiedFieldNameStringSupplier() {
    return (fi) -> {
      return String.format("%s.%s", fi.getProperty()
          .getReadMethod()
          .getDeclaringClass()
          .getSimpleName(), fi.getPropertyName());
    };
  }

  /**
   * @return Returning a sample supplier returning the field name as string.
   */
  public static SampleSupplier<String> fieldNameStringSampleSupplier() {
    return new FunctionSupplier<>(String.class, fieldNameStringSupplier());
  }

  /**
   * @return Returning a sample supplier returning the field name as string.
   */
  public static Function<FieldInfo, String> fieldNameStringSupplier() {
    return (fi) -> {
      return new String(fi.getPropertyName());
    };
  }

  public static SampleSupplier<Long> defaultLongSampleSupplier() {
    return new FunctionSupplier<>(Long.class, defaultLongSupplier());
  }

  public static Function<FieldInfo, Long> defaultLongSupplier() {
    return (fi) -> 0L;
  }

  public static SampleSupplier<Boolean> defaultBooleanSampleSupplier() {
    return new FunctionSupplier<>(Boolean.class, defaultBooleanSupplier());
  }

  public static Function<FieldInfo, Boolean> defaultBooleanSupplier() {
    return (fi) -> false;
  }

  public static SampleSupplier<Character> defaultCharacterSampleSupplier() {
    return new FunctionSupplier<>(Character.class, defaultCharacterSupplier());
  }

  public static Function<FieldInfo, Character> defaultCharacterSupplier() {
    return (fi) -> '\0';
  }

  public static SampleSupplier<Byte> defaultByteSampleSupplier() {
    return new FunctionSupplier<>(Byte.class, defaultByteSupplier());
  }

  public static Function<FieldInfo, Byte> defaultByteSupplier() {
    return (fi) -> (byte) 0;
  }

  public static SampleSupplier<Short> defaultShortSampleSupplier() {
    return new FunctionSupplier<>(Short.class, defaultShortSupplier());
  }

  public static Function<FieldInfo, Short> defaultShortSupplier() {
    return (fi) -> (short) 0;
  }

  public static SampleSupplier<Integer> defaultIntegerSampleSupplier() {
    return new FunctionSupplier<>(Integer.class, defaultIntegerSupplier());
  }

  public static Function<FieldInfo, Integer> defaultIntegerSupplier() {
    return (fi) -> (Integer) 0;
  }

  public static SampleSupplier<Float> defaultFloatSampleSupplier() {
    return new FunctionSupplier<>(Float.class, defaultFloatSupplier());
  }

  public static Function<FieldInfo, Float> defaultFloatSupplier() {
    return (fi) -> (Float) 0f;
  }

  public static SampleSupplier<Double> defaultDoubleSampleSupplier() {
    return new FunctionSupplier<>(Double.class, defaultDoubleSupplier());
  }

  public static Function<FieldInfo, Double> defaultDoubleSupplier() {
    return (fi) -> (Double) 0d;
  }

  public static SampleSupplier<Long> oneLongSampleSupplier() {
    return new FunctionSupplier<>(Long.class, oneLongSupplier());
  }

  public static Function<FieldInfo, Long> oneLongSupplier() {
    return (fi) -> 1L;
  }

  public static SampleSupplier<Boolean> trueBooleanSampleSupplier() {
    return new FunctionSupplier<>(Boolean.class, trueBooleanSupplier());
  }

  public static Function<FieldInfo, Boolean> trueBooleanSupplier() {
    return (fi) -> true;
  }

  public static SampleSupplier<Byte> oneByteSampleSupplier() {
    return new FunctionSupplier<>(Byte.class, oneByteSupplier());
  }

  public static Function<FieldInfo, Byte> oneByteSupplier() {
    return (fi) -> (byte) 1;
  }

  public static SampleSupplier<Short> oneShortSampleSupplier() {
    return new FunctionSupplier<>(Short.class, oneShortSupplier());
  }

  public static Function<FieldInfo, Short> oneShortSupplier() {
    return (fi) -> (short) 1;
  }

  public static SampleSupplier<Integer> oneIntegerSampleSupplier() {
    return new FunctionSupplier<>(Integer.class, oneIntegerSupplier());
  }

  public static Function<FieldInfo, Integer> oneIntegerSupplier() {
    return (fi) -> (Integer) 1;
  }

  public static SampleSupplier<Float> oneFloatSampleSupplier() {
    return new FunctionSupplier<>(Float.class, oneFloatSupplier());
  }

  public static Function<FieldInfo, Float> oneFloatSupplier() {
    return (fi) -> (Float) 1f;
  }

  public static SampleSupplier<Double> oneDoubleSampleSupplier() {
    return new FunctionSupplier<>(Double.class, oneDoubleSupplier());
  }

  public static Function<FieldInfo, Double> oneDoubleSupplier() {
    return (fi) -> (Double) 1d;
  }

  public static SampleSupplier<BigDecimal> defaultBigDecimalSampleSupplier() {
    return new FunctionSupplier<>(BigDecimal.class, defaultBigDecimalSupplier());
  }

  public static Function<FieldInfo, BigDecimal> defaultBigDecimalSupplier() {
    return (fi) -> BigDecimal.ZERO;
  }

  public static SampleSupplier<BigDecimal> oneBigDecimalSampleSupplier() {
    return new FunctionSupplier<>(BigDecimal.class, oneBigDecimalSupplier());
  }

  public static Function<FieldInfo, BigDecimal> oneBigDecimalSupplier() {
    return (fi) -> BigDecimal.ONE;
  }

}

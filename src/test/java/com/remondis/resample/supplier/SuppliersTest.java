package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.dateSupplier;
import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.FieldInfoImpl;
import com.remondis.resample.supplierHierarchy.Enumeration;

public class SuppliersTest {

  @Test
  public void shouldDetectNonEnumValue() {
    Function<FieldInfo, Object> f = enumValueSupplier();
    assertThatThrownBy(() -> {
      f.apply(new FieldInfoImpl("propertyName", LocalDate.class));
    }).hasMessageStartingWith("Cannot supply enum value from non-enum type:")
        .isInstanceOf(IllegalAccessError.class);
  }

  @Test
  public void shouldReturnEnumValue() {
    Function<FieldInfo, Object> f = enumValueSupplier();
    Object retVal = f.apply(new FieldInfoImpl("propertyName", Enumeration.class));
    assertEquals(Enumeration.ENUM_1, retVal);
  }

  @Test
  public void shouldReturnLocalDateNow() {
    LocalDate expected = LocalDate.of(2018, 10, 30);
    Function<FieldInfo, LocalDate> f = localDateSupplier(2018, 10, 30);
    LocalDate retVal = f.apply(new FieldInfoImpl("propertyName", LocalDate.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnLocalDateYesterday() {
    LocalDate expected = LocalDate.of(2018, 10, 30)
        .minus(1, ChronoUnit.DAYS);
    Function<FieldInfo, LocalDate> f = localDateSupplier(2018, 10, 30);
    LocalDate retVal = f.apply(new FieldInfoImpl("start", LocalDate.class));
    assertEquals(expected, retVal);
    retVal = f.apply(new FieldInfoImpl("from", LocalDate.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnLocalDateTomorrow() {
    LocalDate expected = LocalDate.of(2018, 10, 30)
        .plus(1, ChronoUnit.DAYS);
    Function<FieldInfo, LocalDate> f = localDateSupplier(2018, 10, 30);
    LocalDate retVal = f.apply(new FieldInfoImpl("end", LocalDate.class));
    assertEquals(expected, retVal);
    retVal = f.apply(new FieldInfoImpl("to", LocalDate.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnDateNow() {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 2018);
    cal.set(Calendar.MONTH, 10);
    cal.set(Calendar.DAY_OF_MONTH, 30);
    cal.set(Calendar.HOUR_OF_DAY, 10);
    cal.set(Calendar.MINUTE, 10);
    cal.set(Calendar.SECOND, 10);
    cal.set(Calendar.MILLISECOND, 10);
    Date expected = cal.getTime();
    Function<FieldInfo, Date> f = dateSupplier(2018, 10, 30, 10, 10, 10, 10);
    Date retVal = f.apply(new FieldInfoImpl("propertyName", Date.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnDateYesterday() {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 2018);
    cal.set(Calendar.MONTH, 10);
    cal.set(Calendar.DAY_OF_MONTH, 29);
    cal.set(Calendar.HOUR_OF_DAY, 10);
    cal.set(Calendar.MINUTE, 10);
    cal.set(Calendar.SECOND, 10);
    cal.set(Calendar.MILLISECOND, 10);
    Date expected = cal.getTime();
    Function<FieldInfo, Date> f = dateSupplier(2018, 10, 30, 10, 10, 10, 10);
    Date retVal = f.apply(new FieldInfoImpl("from", Date.class));
    assertEquals(expected, retVal);
    retVal = f.apply(new FieldInfoImpl("start", Date.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnDateTomorrow() {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 2018);
    cal.set(Calendar.MONTH, 10);
    cal.set(Calendar.DAY_OF_MONTH, 31);
    cal.set(Calendar.HOUR_OF_DAY, 10);
    cal.set(Calendar.MINUTE, 10);
    cal.set(Calendar.SECOND, 10);
    cal.set(Calendar.MILLISECOND, 10);
    Date expected = cal.getTime();
    Function<FieldInfo, Date> f = dateSupplier(2018, 10, 30, 10, 10, 10, 10);
    Date retVal = f.apply(new FieldInfoImpl("to", Date.class));
    assertEquals(expected, retVal);
    retVal = f.apply(new FieldInfoImpl("end", Date.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnTimeZone() {
    TimeZone expected = TimeZone.getTimeZone("Europe/Berlin");
    Function<FieldInfo, TimeZone> f = Suppliers.timeZoneSupplier("Europe/Berlin");
    TimeZone retVal = f.apply(new FieldInfoImpl("propertyName", TimeZone.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnZonedDateTime() {
    ZonedDateTime expected = ZonedDateTime.of(2018, 10, 30, 15, 10, 5, 1, ZoneId.of("Europe/Berlin"));
    Function<FieldInfo, ZonedDateTime> f = Suppliers.zonedDateTimeSupplier(2018, 10, 30, 15, 10, 5, 1,
        ZoneId.of("Europe/Berlin"));
    ZonedDateTime retVal = f.apply(new FieldInfoImpl("propertyName", TimeZone.class));
    assertEquals(expected, retVal);
  }

  @Test
  public void shouldReturnEmptyString() {
    Supplier<String> s = Suppliers.emptyStringSupplier();
    assertEquals("", s.get());
  }

  @Test
  public void shouldReturnFieldName() {
    Function<FieldInfo, String> f = Suppliers.fieldNameStringSupplier();
    String expected = "propertyName";
    String actual = f.apply(new FieldInfoImpl(expected, String.class));
    assertEquals(expected, actual);
  }

}

package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.dateSupplier;
import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;

import org.junit.Test;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.FieldInfoImpl;

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
    Object retVal = f.apply(new FieldInfoImpl("propertyName", DummyEnum.class));
    assertEquals(DummyEnum.ENUM_1, retVal);
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
  public void stringSupplier_shouldReturnString() throws IntrospectionException {
    FieldInfo field = new FieldInfo(null, new PropertyDescriptor("number", Dummy.class), String.class);

    assertEquals("", Suppliers.emptyStringSampleSupplier()
        .newInstance(field));
    assertEquals("number", Suppliers.fieldNameStringSampleSupplier()
        .newInstance(field));
    assertEquals("com.remondis.resample.supplier.Dummy.number", Suppliers.fullyQualifiedFieldNameStringSampleSupplier()
        .newInstance(field));
    assertEquals("Dummy.number", Suppliers.shortQualifiedFieldNameStringSampleSupplier()
        .newInstance(field));
  }

  @Test
  public void bigDecimalSupplier_shouldProvideBigDecimal() throws IntrospectionException {
    FieldInfo field = new FieldInfo(null, new PropertyDescriptor("number", Dummy.class), BigDecimal.class);

    assertEquals(BigDecimal.valueOf(42), Suppliers.bigDecimalSampleSupplier(42)
        .newInstance(field));
    assertEquals(BigDecimal.valueOf(42.11), Suppliers.bigDecimalSampleSupplier(42.11)
        .newInstance(field));
    assertEquals(BigDecimal.ONE, Suppliers.oneBigDecimalSampleSupplier()
        .newInstance(field));
    assertEquals(BigDecimal.ZERO, Suppliers.defaultBigDecimalSampleSupplier()
        .newInstance(field));
  }

  @Test
  public void bigIntegerSupplier_shouldProvideBigInteger() throws IntrospectionException {
    FieldInfo field = new FieldInfo(null, new PropertyDescriptor("number", Dummy.class), BigInteger.class);

    assertEquals(BigInteger.valueOf(42), Suppliers.bigIntegerSampleSupplier(42)
        .newInstance(field));
    assertEquals(BigInteger.ONE, Suppliers.oneBigIntegerSampleSupplier()
        .newInstance(field));
    assertEquals(BigInteger.ZERO, Suppliers.defaultBigIntegerSampleSupplier()
        .newInstance(field));
  }

  @Test
  public void nullSupplier_shouldProvideTypedNull() throws IntrospectionException {
    FieldInfo numberField = new FieldInfo(null, new PropertyDescriptor("number", Dummy.class), BigDecimal.class);
    FieldInfo stringField = new FieldInfo(null, new PropertyDescriptor("number", Dummy.class), String.class);
    FieldInfo dummyField = new FieldInfo(null, new PropertyDescriptor("number", Dummy.class), Dummy.class);

    assertNull(Suppliers.nullValueSampleSupplier(BigDecimal.class)
        .newInstance(numberField));
    assertNull(Suppliers.nullValueSampleSupplier(String.class)
        .newInstance(stringField));
    assertNull(Suppliers.nullValueSampleSupplier(Dummy.class)
        .newInstance(dummyField));
  }
}

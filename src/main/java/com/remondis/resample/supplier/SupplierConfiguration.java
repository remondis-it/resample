package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.dateSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultBigDecimalSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;
import static com.remondis.resample.supplier.Suppliers.timeZoneSupplier;
import static com.remondis.resample.supplier.Suppliers.zonedDateTimeSupplier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.remondis.resample.FunctionSupplier;

/**
 * A configuration for an Application Context providing the most common suppliers for JDK classes.
 */
@Configuration
public class SupplierConfiguration {

  @Bean
  public FunctionSupplier<BigDecimal> beanBigDecimalSupplier() {
    return new FunctionSupplier<>(BigDecimal.class, defaultBigDecimalSupplier());
  }

  @Bean
  public FunctionSupplier<Date> beanDateSupplier() {
    return new FunctionSupplier<>(Date.class, dateSupplier());
  }

  @Bean
  public FunctionSupplier<LocalDate> beanLocalDateSupplier() {
    return new FunctionSupplier<>(LocalDate.class, localDateSupplier());
  }

  @Bean
  public FunctionSupplier<ZonedDateTime> beanZonedDateTimeSupplier() {
    return new FunctionSupplier<>(ZonedDateTime.class, zonedDateTimeSupplier());
  }

  @Bean
  public FunctionSupplier<TimeZone> beanTimeZoneSupplier() {
    return new FunctionSupplier<>(TimeZone.class, timeZoneSupplier());
  }

  @Bean
  public FunctionSupplier<String> beanStringFieldNameSupplier() {
    return new FunctionSupplier<>(String.class, fieldNameStringSupplier());
  }
}

package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;
import static com.remondis.resample.supplier.Suppliers.timeZoneSupplier;
import static com.remondis.resample.supplier.Suppliers.zonedDateTimeSupplier;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.remondis.resample.FunctionSupplier;

@Configuration
public class SupplierConfiguration {

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

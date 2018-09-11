package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.localDateSupplier;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.remondis.resample.FunctionSupplier;

@Component
public class LocalDateSupplier extends FunctionSupplier<LocalDate> {
  public LocalDateSupplier() {
    super(LocalDate.class, localDateSupplier());
  }
}

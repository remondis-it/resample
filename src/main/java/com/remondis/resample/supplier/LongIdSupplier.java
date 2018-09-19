package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.defaultLongSupplier;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.FunctionSupplier;

@Component
public class LongIdSupplier extends FunctionSupplier<Long> {

  public LongIdSupplier() {
    super(Long.class, longIdSupplier());
  }

  public static Function<FieldInfo, Long> longIdSupplier() {
    return (fi) -> {
      if (fi.getPropertyName()
          .toLowerCase()
          .equals("id")) {
        return 1L;
      } else {
        return defaultLongSupplier().apply(fi);
      }
    };
  }
}

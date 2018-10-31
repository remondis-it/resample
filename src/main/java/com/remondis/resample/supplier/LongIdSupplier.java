package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.defaultLongSupplier;

import java.util.function.Function;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.FunctionSupplier;

/**
 * A supplier implementation that generates {@link Long} values. This supplier uses the
 * {@link Suppliers#defaultLongSupplier()} to generate values, but if the field is named 'id' the supplier will return
 * <code>1L</code>. This is a reasonable value for ids when working with databases.
 *
 */
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

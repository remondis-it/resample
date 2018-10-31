package com.remondis.resample;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static java.util.Objects.requireNonNull;

import com.remondis.resample.supplier.Suppliers;

/**
 * The {@link SampleService} is a facade to build a default configuration for {@link Sample}.
 * The resulting {@link Sample} instance uses the following
 * configuration:
 * <ul>
 * - The {@link Sample} instance denies <code>null</code> fields.
 * - The default enum value supplier is used.
 * - The {@link Suppliers#fieldNameStringSupplier()} is used for {@link String}
 * - Auto-sampling is active, trying to build dependencies recursively with the same configuration.
 * </ul>
 */
public class SampleService {

  public SampleService() {
    super();
  }

  /**
   * Returns a {@link Sample} for the specified type.
   *
   * @param type The type to generate sample data for.
   * @return Returns {@link Sample}.
   */
  public <T> Sample<T> of(Class<T> type) {
    requireNonNull(type, "Type must not be null.");
    Sample<T> sample = Samples.of(type)
        .checkForNullFields()
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .useForEnum(enumValueSupplier())
        .useAutoSampling();
    return sample;
  }

}

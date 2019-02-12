package com.remondis.resample;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;

import com.remondis.resample.supplier.Suppliers;

/**
 * API to create and configure instances of {@link Sample}.
 */
public class Samples {
  /**
   * Creates a new {@link Sample} of the specified type.
   *
   * @param type The type of objects to generate.
   * @return Returns a {@link Sample} instance for further configuration.
   */
  public static <T> Sample<T> of(Class<T> type) {
    return new Sample<>(type);
  }

  /**
   * Provides access to a pre-configured {@link Sample} configuration.
   */
  static class Default {

    /**
     * Creates a new {@link Sample} of the specified type using a default configuration.
     *
     * <p>
     * The default configuration specifies the following:
     * <ul>
     * <li>denies <code>null</code>-fields</li>
     * <li>uses {@link Suppliers#fieldNameStringSupplier()}</li>
     * <li>uses {@link Suppliers#enumValueSupplier()}</li>
     * <li><b>activates auto-sampling</b></li>
     * </ul>
     * </p>
     *
     * @param type The type of objects to generate.
     * @return Returns a {@link Sample} instance for further configuration.
     */
    public static <T> Sample<T> of(Class<T> type) {
      return Samples.of(type)
          .checkForNullFields()
          .use(fieldNameStringSupplier())
          .forType(String.class)
          .useForEnum(enumValueSupplier())
          .useAutoSampling();
    }
  }

}

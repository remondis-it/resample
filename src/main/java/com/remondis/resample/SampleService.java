package com.remondis.resample;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.remondis.resample.supplier.SupplierConfiguration;
import com.remondis.resample.supplier.Suppliers;

/**
 * The {@link SampleService} is a facade to build a default configuration for {@link Sample}. This facade can be used in
 * a Spring Application Context or in a static way. The resulting {@link Sample} instance uses the following
 * configuration:
 * <ul>
 * - The Application Context is used if available
 * - The {@link SupplierConfiguration} is put into the Application context if available.
 * - The {@link Sample} instance denies <code>null</code> fields.
 * - The default enum value supplier is used.
 * - The {@link Suppliers#fieldNameStringSupplier()} is used for {@link String}
 * - Auto-sampling is active, trying to build dependencies recursively with the same configuration.
 * </ul>
 */
@Service
@Import({
    SupplierConfiguration.class
})
public class SampleService {

  private ApplicationContext ctx;

  public SampleService() {
    super();
  }

  @Autowired
  public SampleService(ApplicationContext ctx) {
    super();
    this.ctx = ctx;
  }

  /**
   * Returns a {@link Sample} for the specified type.
   * 
   * @param type The type to generate sample data for.
   * @return Returns {@link Sample}.
   */
  public <T> Sample<T> of(Class<T> type) {
    requireNonNull(type, "Type must not be null.");
    Sample<T> sample = getSample(type);
    if (nonNull(ctx)) {
      sample.useApplicationContext(ctx);
    }
    return sample;
  }

  /**
   * Returns a {@link Sample} for the specified type.
   * 
   * @param type The type to generate sample data for.
   * @return Returns {@link Sample}.
   */
  public static <T> Sample<T> getSample(Class<T> type) {
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

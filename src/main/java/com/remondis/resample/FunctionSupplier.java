package com.remondis.resample;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * This is a base class for function suppliers. They can be useful to provide a component for an Application Context
 * providing sample data.
 *
 * @param <T> The type this supplier generates.
 */
public class FunctionSupplier<T> extends AbstractSampleSupplier<T> {

  private Function<FieldInfo, T> supplierFunction;

  /**
   * Creates a new function supplier for the specified type and supplier function.
   *
   * @param type The type used by {@link SettingBuilder#forType(Class)}.
   * @param supplierFunction The supplier function.
   */
  public FunctionSupplier(Class<T> type, Function<FieldInfo, T> supplierFunction) {
    super(type);
    requireNonNull(supplierFunction, "Supplier function must not be null.");
    this.supplierFunction = supplierFunction;
  }

  @Override
  public T newInstance(FieldInfo fieldInfo) {
    return supplierFunction.apply(fieldInfo);
  }

  /**
   * @return Returns the supplier function.
   */
  public final Function<FieldInfo, T> function() {
    return supplierFunction;
  }
}

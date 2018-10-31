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

  public FunctionSupplier(Class<T> type, Function<FieldInfo, T> supplierFunction) {
    super(type);
    requireNonNull(supplierFunction, "Supplier function must not be null.");
    this.supplierFunction = supplierFunction;
  }

  @Override
  public T newInstance(FieldInfo fieldInfo) {
    return supplierFunction.apply(fieldInfo);
  }
}

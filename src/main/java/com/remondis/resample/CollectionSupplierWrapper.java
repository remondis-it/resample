package com.remondis.resample;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.function.Function;

public class CollectionSupplierWrapper<S> implements Function<FieldInfo, Collection<S>> {

  private Function<FieldInfo, S> supplier;
  private Class<?> propertyType;

  public CollectionSupplierWrapper(Class<?> propertyType, Function<FieldInfo, S> supplier) {
    this.propertyType = propertyType;
    this.supplier = supplier;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<S> apply(FieldInfo fi) {
    return (Collection<S>) asList(supplier.apply(fi)).stream()
        .collect(ReflectionUtil.getCollector(propertyType));
  }

}

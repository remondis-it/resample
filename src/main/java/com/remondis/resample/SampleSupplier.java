package com.remondis.resample;

public interface SampleSupplier<T> {

  public Class<T> getType();

  public T newInstance(FieldInfo fieldInfo);
}

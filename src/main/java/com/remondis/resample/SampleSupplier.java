package com.remondis.resample;

/**
 * The {@link SampleSupplier} is a supertype for sample instance factories. The contract is that
 * {@link #newInstance(FieldInfo)} should return new instances on each call of type {@link #getType()}.
 * 
 * @param <T>
 */
public interface SampleSupplier<T> {

  public Class<T> getType();

  public T newInstance(FieldInfo fieldInfo);
}

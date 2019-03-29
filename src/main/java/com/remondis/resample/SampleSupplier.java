package com.remondis.resample;

/**
 * The {@link SampleSupplier} is a supertype for sample instance factories. The contract is that
 * {@link #newInstance(FieldInfo)} should return new instances on each call of type {@link #getType()}.
 *
 * @param <T>
 */
public interface SampleSupplier<T> {

  /**
   * @return Returns the type, this supplier generates data for.
   */
  public Class<T> getType();

  /**
   * Implementations should create a new instance of the specified type.
   *
   * @param fieldInfo Information about the field for which the data is to be created.
   * @return Returns a new instance of the specified type.
   */
  public T newInstance(FieldInfo fieldInfo);
}

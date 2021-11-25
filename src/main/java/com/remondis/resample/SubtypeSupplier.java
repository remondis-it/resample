package com.remondis.resample;

/**
 * Facade to create subtypes using an existing configuration of {@link Sample}.
 */
public interface SubtypeSupplier {

  /**
   * Creates a subtype using the current configuration of {@link Sample}.
   *
   * @param <T> The subtype.
   * @param fieldInfo The {@link FieldInfo} of the field to create an instance for.
   * @param subtype The subtype.
   * @return New instance of subtype.
   */
  public <T> T createSubtype(FieldInfo fieldInfo, Class<T> subtype);

}

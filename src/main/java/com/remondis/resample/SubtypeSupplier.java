package com.remondis.resample;

/**
 * Facade to create subtypes using an existing configuration of {@link Sample}.
 */
public interface SubtypeSupplier {

  /**
   * Creates a subtype using the current configuration of {@link Sample}.
   *
   * @param <T> The subtype.
   * @param subtype The subtype.
   * @return New instance of subtype.
   */
  public <T> T createSubtype(Class<T> subtype);

}

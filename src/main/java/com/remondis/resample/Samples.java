package com.remondis.resample;

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
}

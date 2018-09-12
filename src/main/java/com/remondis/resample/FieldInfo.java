package com.remondis.resample;

/**
 * Contains some information about the field for which sample data should be
 * generated.
 */
public class FieldInfo {

  private String propertyName;
  private Class<?> type;

  FieldInfo(String propertyName, Class<?> type) {
    super();
    this.propertyName = propertyName;
    this.type = type;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public Class<?> getType() {
    return type;
  }

}

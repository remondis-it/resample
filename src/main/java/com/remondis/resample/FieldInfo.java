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

  /**
   * @return Returns the simple property name of the field.
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * @return Returns the type of the field.
   */
  public Class<?> getType() {
    return type;
  }

}

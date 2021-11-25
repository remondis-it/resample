package com.remondis.resample;

import java.beans.PropertyDescriptor;

/**
 * Contains some information about the field for which sample data should be
 * generated.
 */
public class FieldInfo {

  private PropertyDescriptor property;
  private Class<?> type;
  private SubtypeSupplier subtypeSupplier;

  /**
   * Creates a new {@link FieldInfo}.
   */
  public FieldInfo(SubtypeSupplier subtypeSupplier, PropertyDescriptor pd, Class<?> type) {
    super();
    this.subtypeSupplier = subtypeSupplier;
    this.property = pd;
    this.type = type;
  }

  public SubtypeSupplier getSubtypeSupplier() {
    return subtypeSupplier;
  }

  /**
   * @return Returns the simple property name of the field.
   */
  public String getPropertyName() {
    return property.getName();
  }

  /**
   * @return Returns the {@link PropertyDescriptor} of the field.
   */
  public PropertyDescriptor getProperty() {
    return property;
  }

  /**
   * @return Returns the type of the field.
   */
  public Class<?> getType() {
    return type;
  }

}

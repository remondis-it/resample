package com.remondis.resample;

import java.beans.PropertyDescriptor;

public class FieldInfoImpl extends FieldInfo {

  private String nameOverride;

  public FieldInfoImpl(PropertyDescriptor pd, Class<?> type) {
    super(null, pd, type);
  }

  public FieldInfoImpl(String nameOverride, Class<?> type) {
    super(null, null, type);
    this.nameOverride = nameOverride;
  }

  @Override
  public String getPropertyName() {
    return nameOverride;
  }

}

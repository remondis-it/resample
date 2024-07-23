package com.remondis.resample;

import java.util.Objects;

public class Dummy {

  private String field;

  private String anotherField;

  public Dummy(String field, String anotherField) {
    super();
    this.field = field;
    this.anotherField = anotherField;
  }

  public Dummy() {
    super();
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getAnotherField() {
    return anotherField;
  }

  public void setAnotherField(String anotherField) {
    this.anotherField = anotherField;
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, anotherField);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Dummy other = (Dummy) obj;
    return Objects.equals(field, other.field) && Objects.equals(anotherField, other.anotherField);
  }

  @Override
  public String toString() {
    return "Dummy [field=" + field + ", anotherField=" + anotherField + "]";
  }

}

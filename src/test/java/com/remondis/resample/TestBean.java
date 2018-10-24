package com.remondis.resample;

import java.util.List;

import com.remondis.resample.lists.Dummy;

public class TestBean {

  private List<String> strings;
  private List<Dummy> dummies;
  private Boolean wrapperBoolean;
  private boolean primitiveBoolean;
  private String string;
  private Long wrapperLong;
  private long primitiveLong;

  public TestBean(List<String> strings, List<Dummy> dummies, Boolean wrapperBoolean, boolean primitiveBoolean,
      String string, Long wrapperLong, long primitiveLong) {
    super();
    this.strings = strings;
    this.dummies = dummies;
    this.wrapperBoolean = wrapperBoolean;
    this.primitiveBoolean = primitiveBoolean;
    this.string = string;
    this.wrapperLong = wrapperLong;
    this.primitiveLong = primitiveLong;
  }

  public TestBean() {
    super();
  }

  public String getNotARealProperty() {
    return "";
  }

  public void getWithoutReturnValue() {

  }

  public String regularMethod() {
    return "";
  }

  public List<String> getStrings() {
    return strings;
  }

  public void setStrings(List<String> strings) {
    this.strings = strings;
  }

  public List<Dummy> getDummies() {
    return dummies;
  }

  public void setDummies(List<Dummy> dummies) {
    this.dummies = dummies;
  }

  public Boolean getWrapperBoolean() {
    return wrapperBoolean;
  }

  public void setWrapperBoolean(Boolean wrapperBoolean) {
    this.wrapperBoolean = wrapperBoolean;
  }

  public boolean isPrimitiveBoolean() {
    return primitiveBoolean;
  }

  public void setPrimitiveBoolean(boolean primitiveBoolean) {
    this.primitiveBoolean = primitiveBoolean;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Long getWrapperLong() {
    return wrapperLong;
  }

  public void setWrapperLong(Long wrapperLong) {
    this.wrapperLong = wrapperLong;
  }

  public long getPrimitiveLong() {
    return primitiveLong;
  }

  public void setPrimitiveLong(long primitiveLong) {
    this.primitiveLong = primitiveLong;
  }

  @Override
  public String toString() {
    return "TestBean [strings=" + strings + ", dummies=" + dummies + ", wrapperBoolean=" + wrapperBoolean
        + ", primitiveBoolean=" + primitiveBoolean + ", string=" + string + ", wrapperLong=" + wrapperLong
        + ", primitiveLong=" + primitiveLong + "]";
  }

}

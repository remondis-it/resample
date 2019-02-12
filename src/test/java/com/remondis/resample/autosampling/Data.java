package com.remondis.resample.autosampling;

import java.math.BigDecimal;

public class Data {

  private BigDecimal id;

  public Data() {
    super();
  }

  public Data(BigDecimal id) {
    super();
    this.id = id;
  }

  public BigDecimal getId() {
    return id;
  }

  public void setId(BigDecimal id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Data [id=" + id + "]";
  }

}

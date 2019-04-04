package com.remondis.resample.regression;

import java.time.ZonedDateTime;

public class Dummy {
  private ZonedDateTime validFrom;
  private ZonedDateTime validTo;

  public Dummy(ZonedDateTime validFrom, ZonedDateTime validTo) {
    super();
    this.validFrom = validFrom;
    this.validTo = validTo;
  }

  public Dummy() {
    super();
  }

  public ZonedDateTime getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(ZonedDateTime validFrom) {
    this.validFrom = validFrom;
  }

  public ZonedDateTime getValidTo() {
    return validTo;
  }

  public void setValidTo(ZonedDateTime validTo) {
    this.validTo = validTo;
  }

}

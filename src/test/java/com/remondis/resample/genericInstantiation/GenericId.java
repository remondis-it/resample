package com.remondis.resample.genericInstantiation;

import java.io.Serializable;

public class GenericId<ID extends Serializable> {

  private ID id;

  public GenericId(ID id) {
    super();
    this.id = id;
  }

  public GenericId() {
    super();
  }

  public ID getId() {
    return id;
  }

  public void setId(ID id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "GenericId [id=" + id + "]";
  }

}

package com.remondis.resample.genericInstantiation;

import java.util.List;

public class Person {

  private GenericId<Long> id;

  private List<GenericId<Long>> genericIds;

  public Person() {
    super();
  }

  public Person(GenericId<Long> id, List<GenericId<Long>> genericIds) {
    super();
    this.id = id;
    this.genericIds = genericIds;
  }

  public GenericId<Long> getId() {
    return id;
  }

  public void setId(GenericId<Long> id) {
    this.id = id;
  }

  public List<GenericId<Long>> getGenericIds() {
    return genericIds;
  }

  public void setGenericIds(List<GenericId<Long>> genericIds) {
    this.genericIds = genericIds;
  }

  @Override
  public String toString() {
    return "Person [id=" + id + "]";
  }

}

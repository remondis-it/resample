package com.remondis.resample.genericInstantiation;

public class Person {

  private GenericId<Long> id;

  public Person(GenericId<Long> id) {
    super();
    this.id = id;
  }

  public Person() {
    super();
  }

  public GenericId<Long> getId() {
    return id;
  }

  public void setId(GenericId<Long> id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Person [id=" + id + "]";
  }

}

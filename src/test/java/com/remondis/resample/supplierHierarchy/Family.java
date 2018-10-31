package com.remondis.resample.supplierHierarchy;

import java.util.List;

public class Family {

  private List<Person> persons;

  public Family(List<Person> persons) {
    super();
    this.persons = persons;
  }

  public Family() {
    super();
  }

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }

}

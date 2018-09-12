package com.remondis.resample;

import java.util.List;
import java.util.Set;

import com.remondis.example.Gender;

public class ListDto {

  private List<Gender> listOfEnumValues;
  private Set<Gender> setOfEnumValues;

  public ListDto() {
    super();
  }

  public List<Gender> getListOfEnumValues() {
    return listOfEnumValues;
  }

  public void setListOfEnumValues(List<Gender> listOfEnumValues) {
    this.listOfEnumValues = listOfEnumValues;
  }

  public Set<Gender> getSetOfEnumValues() {
    return setOfEnumValues;
  }

  public void setSetOfEnumValues(Set<Gender> setOfEnumValues) {
    this.setOfEnumValues = setOfEnumValues;
  }

}

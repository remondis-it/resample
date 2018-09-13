package com.remondis.resample;

import java.util.List;
import java.util.Set;

import com.remondis.example.Gender;

public class ListDto {

  private List<Gender> listOfEnumValues;
  private Set<Gender> setOfEnumValues;

  private Set<Long> setOfLongs;

  private Set<String> setOfStrings;

  private Set<Dummy> setOfDummies;

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

  public Set<Long> getSetOfLongs() {
    return setOfLongs;
  }

  public void setSetOfLongs(Set<Long> setOfLongs) {
    this.setOfLongs = setOfLongs;
  }

  public Set<String> getSetOfStrings() {
    return setOfStrings;
  }

  public void setSetOfStrings(Set<String> setOfStrings) {
    this.setOfStrings = setOfStrings;
  }

  public Set<Dummy> getSetOfDummies() {
    return setOfDummies;
  }

  public void setSetOfDummies(Set<Dummy> setOfDummies) {
    this.setOfDummies = setOfDummies;
  }

}

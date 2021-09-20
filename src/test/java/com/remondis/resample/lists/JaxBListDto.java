package com.remondis.resample.lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JAXB DTO having no setters for lists.
 */
class JaxBListDto {

  private String justAnyString;

  private final List<Gender> listOfEnumValues = new ArrayList<>();

  private final Set<Gender> setOfEnumValues = new HashSet<>();

  private final List<Long> listOfLongs = new ArrayList<>();

  private final Set<Long> setOfLongs = new HashSet<>();

  private final List<String> listOfStrings = new ArrayList<>();

  private final Set<String> setOfStrings = new HashSet<>();

  private final List<Dummy> listOfDummies = new ArrayList<>();

  private final Set<Dummy> setOfDummies = new HashSet<>();

  public JaxBListDto() {
    super();
  }

  public String getJustAnyString() {
    return justAnyString;
  }

  public void setJustAnyString(String justAnyString) {
    this.justAnyString = justAnyString;
  }

  public List<Gender> getListOfEnumValues() {
    return listOfEnumValues;
  }

  public Set<Gender> getSetOfEnumValues() {
    return setOfEnumValues;
  }

  public List<Long> getListOfLongs() {
    return listOfLongs;
  }

  public Set<Long> getSetOfLongs() {
    return setOfLongs;
  }

  public List<String> getListOfStrings() {
    return listOfStrings;
  }

  public Set<String> getSetOfStrings() {
    return setOfStrings;
  }

  public List<Dummy> getListOfDummies() {
    return listOfDummies;
  }

  public Set<Dummy> getSetOfDummies() {
    return setOfDummies;
  }

  @Override
  public String toString() {
    return "PrivateListDto [listOfEnumValues=" + listOfEnumValues + ", setOfEnumValues=" + setOfEnumValues
        + ", listOfLongs=" + listOfLongs + ", setOfLongs=" + setOfLongs + ", listOfStrings=" + listOfStrings
        + ", setOfStrings=" + setOfStrings + ", listOfDummies=" + listOfDummies + ", setOfDummies=" + setOfDummies
        + "]";
  }

}

package com.remondis.resample.maps;

public class Value {

  private Long id;
  private String description;

  public Value() {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Value [id=" + id + ", description=" + description + "]";
  }

}

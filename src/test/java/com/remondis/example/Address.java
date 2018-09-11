package com.remondis.example;

public class Address {

  private String street;

  private String houseNumber;

  private String city;

  private String zipCode;

  private String countryCode;

  public Address(String street, String houseNumber, String city, String zipCode, String countryCode) {
    super();
    this.street = street;
    this.houseNumber = houseNumber;
    this.city = city;
    this.zipCode = zipCode;
    this.countryCode = countryCode;
  }

  public Address() {
    super();
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
    result = prime * result + ((houseNumber == null) ? 0 : houseNumber.hashCode());
    result = prime * result + ((street == null) ? 0 : street.hashCode());
    result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Address other = (Address) obj;
    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
      return false;
    if (countryCode == null) {
      if (other.countryCode != null)
        return false;
    } else if (!countryCode.equals(other.countryCode))
      return false;
    if (houseNumber == null) {
      if (other.houseNumber != null)
        return false;
    } else if (!houseNumber.equals(other.houseNumber))
      return false;
    if (street == null) {
      if (other.street != null)
        return false;
    } else if (!street.equals(other.street))
      return false;
    if (zipCode == null) {
      if (other.zipCode != null)
        return false;
    } else if (!zipCode.equals(other.zipCode))
      return false;
    return true;
  }

}

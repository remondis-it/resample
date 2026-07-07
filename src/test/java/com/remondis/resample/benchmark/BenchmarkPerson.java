package com.remondis.resample.benchmark;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A bean with primitives, wrappers, strings, enums, collections, maps and transitive object references, used to
 * benchmark auto-sampling of a whole object graph.
 */
public class BenchmarkPerson {

  private long id;
  private String firstName;
  private String lastName;
  private int age;
  private boolean active;
  private Gender gender;
  private List<String> nicknames;
  private Set<Long> favoriteNumbers;
  private BenchmarkAddress address;
  private List<BenchmarkAddress> previousAddresses;
  private Map<String, BenchmarkAddress> addressesByLabel;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public List<String> getNicknames() {
    return nicknames;
  }

  public void setNicknames(List<String> nicknames) {
    this.nicknames = nicknames;
  }

  public Set<Long> getFavoriteNumbers() {
    return favoriteNumbers;
  }

  public void setFavoriteNumbers(Set<Long> favoriteNumbers) {
    this.favoriteNumbers = favoriteNumbers;
  }

  public BenchmarkAddress getAddress() {
    return address;
  }

  public void setAddress(BenchmarkAddress address) {
    this.address = address;
  }

  public List<BenchmarkAddress> getPreviousAddresses() {
    return previousAddresses;
  }

  public void setPreviousAddresses(List<BenchmarkAddress> previousAddresses) {
    this.previousAddresses = previousAddresses;
  }

  public Map<String, BenchmarkAddress> getAddressesByLabel() {
    return addressesByLabel;
  }

  public void setAddressesByLabel(Map<String, BenchmarkAddress> addressesByLabel) {
    this.addressesByLabel = addressesByLabel;
  }

}

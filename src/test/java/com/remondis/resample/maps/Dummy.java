package com.remondis.resample.maps;

import java.util.HashMap;
import java.util.Map;

public class Dummy {

  private Map<Enum, Value> map;

  public Dummy() {
    super();
    this.map = new HashMap<>();
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public Value put(Enum key, Value value) {
    return map.put(key, value);
  }

  public void clear() {
    map.clear();
  }

  public Map<Enum, Value> getMap() {
    return map;
  }

  public void setMap(Map<Enum, Value> map) {
    this.map = map;
  }

  @Override
  public String toString() {
    return "Dummy [map=" + map + "]";
  }

}

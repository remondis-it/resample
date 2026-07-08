package com.remondis.resample;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {

  @Test
  public void shouldDetermineListGenericType() {
    Class<?> collectionType = ReflectionUtil.getCollectionType(
        ReflectionUtil.getPropertyDescriptorBySensorCall(ListDto.class, ListDto::getListOfEnumValues));
    assertEquals(Gender.class, collectionType);
  }

}

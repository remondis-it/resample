package com.remondis.resample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.resample.lists.Gender;
import com.remondis.resample.lists.ListDto;

public class ReflectionUtilTest {

  @Test
  public void shouldDetermineListGenericType() {
    Class<?> collectionType = ReflectionUtil.getCollectionType(
        ReflectionUtil.getPropertyDescriptorBySensorCall(ListDto.class, ListDto::getListOfEnumValues));
    assertEquals(Gender.class, collectionType);
  }

}

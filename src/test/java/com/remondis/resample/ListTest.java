package com.remondis.resample;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ListTest {

  @Test
  public void shouldGenerateList() {
    ListDto instance = Samples.of(ListDto.class)
        .useForEnum(enumValueSupplier())
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .useAutoSampling()
        .newInstance();
    assertNotNull(instance.getListOfEnumValues());
    assertNotNull(instance.getSetOfEnumValues());
    assertNotNull(instance.getSetOfStrings());
    assertNotNull(instance.getSetOfLongs());
    assertNotNull(instance.getSetOfDummies());
  }

  @Test
  public void shouldDetermineListGenericType() {
    Class<?> collectionType = ReflectionUtil.getCollectionType(
        ReflectionUtil.getPropertyDescriptorBySensorCall(ListDto.class, ListDto::getListOfEnumValues));
    assertEquals(Gender.class, collectionType);
  }

}

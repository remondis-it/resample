package com.remondis.resample;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ListTest {

  @Test
  public void shouldGenerateList() {
    ListDto instance = Sample.of(ListDto.class)
        .useForEnum(enumValueSupplier())
        .useAutoSampling()
        .newInstance();
    assertNotNull(instance.getListOfEnumValues());
  }

}

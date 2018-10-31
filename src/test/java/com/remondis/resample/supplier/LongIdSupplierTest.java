package com.remondis.resample.supplier;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.FieldInfoImpl;

public class LongIdSupplierTest {

  @Test
  public void shouldReturn1LforIds() {
    Function<FieldInfo, Long> function = LongIdSupplier.longIdSupplier();
    Long retVal = function.apply(new FieldInfoImpl("id", Long.class));
    assertEquals(1L, (long) retVal);
  }

  @Test
  public void shouldReturn0LforOtherFieldNames() {
    Function<FieldInfo, Long> function = LongIdSupplier.longIdSupplier();
    Long retVal = function.apply(new FieldInfoImpl("propertyName", Long.class));
    assertEquals(0L, (long) retVal);
  }

}

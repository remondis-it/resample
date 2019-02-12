package com.remondis.resample.maps;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.remondis.resample.SampleException;
import com.remondis.resample.Samples;

public class MapTest {

  @Test
  public void shouldLeaveMapIfKeyCannotBeSampled() {
    assertThatThrownBy(() -> Samples.of(Dummy.class)
        .checkForNullFields()
        .useForEnum(enumValueSupplier())
        .get()).isInstanceOf(SampleException.class)
            .hasMessageContaining(
                "- map, accessed by java.util.Map getMap(), class: class com.remondis.resample.maps.Dummy");
  }

  @Test
  public void shouldLeaveMapIfValueCannotBeSampled() {
    assertThatThrownBy(() -> Samples.of(Dummy.class)
        .checkForNullFields()
        .use(() -> new Value())
        .forType(Value.class)
        .get()).isInstanceOf(SampleException.class)
            .hasMessageContaining(
                "- map, accessed by java.util.Map getMap(), class: class com.remondis.resample.maps.Dummy");
  }

  @Test
  public void shouldSampleMap() {
    Dummy dummy = Samples.Default.of(Dummy.class)
        .get();
    assertNotNull(dummy.getMap());
    assertFalse(dummy.getMap()
        .isEmpty());
  }

}

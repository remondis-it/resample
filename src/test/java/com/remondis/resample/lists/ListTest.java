package com.remondis.resample.lists;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Test;

import com.remondis.resample.Samples;

public class ListTest {

  @Test
  public void shouldGenerateList() {
    ListDto instance = Samples.of(ListDto.class)
        .useForEnum(enumValueSupplier())
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .useAutoSampling()
        .newInstance();
    assertThat(instance.getListOfEnumValues(), is(asList(Gender.MALE)));
    assertThat(instance.getSetOfStrings(), is(new HashSet<>(asList("setOfStrings"))));
    assertThat(instance.getSetOfEnumValues(), is(new HashSet<>(asList(Gender.MALE))));
    assertThat(instance.getSetOfStrings(), is(new HashSet<>(asList("setOfStrings"))));
    assertThat(instance.getSetOfLongs(), is(new HashSet<>(asList(0L))));
    assertThat(instance.getSetOfDummies(), is(new HashSet<>(asList(new Dummy("field")))));
  }

}

package com.remondis.resample.lists;

import static com.remondis.resample.CollectionSamplingMode.USE_GETTER_AND_ADD;
import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import com.remondis.resample.CollectionSamplingMode;
import com.remondis.resample.Samples;
import org.junit.Test;

public class JaxBListTest {

  @Test
  public void shouldGenerateList() {
    JaxBListDto instance = Samples
        .of(JaxBListDto.class)
        .collectionSamplingMode(USE_GETTER_AND_ADD)
        .useForEnum(enumValueSupplier())
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .useAutoSampling()
        .newInstance();

    assertThat(instance.getJustAnyString(), is("justAnyString"));
    assertThat(instance.getListOfEnumValues(), is(asList(Gender.MALE)));
    assertThat(instance.getSetOfEnumValues(), is(new HashSet<>(asList(Gender.MALE))));
    assertThat(instance.getListOfStrings(), is(asList("listOfStrings")));
    assertThat(instance.getSetOfStrings(), is(new HashSet<>(asList("setOfStrings"))));
    assertThat(instance.getListOfLongs(), is(asList(0L)));
    assertThat(instance.getSetOfLongs(), is(new HashSet<>(asList(0L))));
    assertThat(instance.getListOfDummies(), is(asList(new Dummy("field"))));
    assertThat(instance.getSetOfDummies(), is(new HashSet<>(asList(new Dummy("field")))));
  }

}

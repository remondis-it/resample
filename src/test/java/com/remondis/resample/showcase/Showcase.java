package com.remondis.resample.showcase;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;

import org.junit.Test;

import com.remondis.resample.Samples;

public class Showcase {

  @Test
  public void shouldGenerateSampleData() {
    Person person = Samples.of(Person.class)
        .checkForNullFields()
        .useForEnum(enumValueSupplier())
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .use(localDateSupplier(2018, 8, 30))
        .forField(Person::getBrithday)
        .newInstance();
    System.out.println(person);
  }

}

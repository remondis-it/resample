package com.remondis.resample.showcase;

import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;

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
    assertNotNull(person);
    assertEquals("name", person.getName());
    assertEquals("forname", person.getForname());
    assertEquals(0, person.getAge());
    assertEquals(LocalDate.of(2018, 8, 30), person.getBrithday());
    assertEquals(Gender.MALE, person.getGender());
  }

}

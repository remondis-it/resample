package com.remondis.resample.supplierHierarchy;

import static com.remondis.resample.supplier.LongIdSupplier.longIdSampleSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultBooleanSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultByteSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultCharacterSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultDoubleSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultFloatSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultIntegerSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultLongSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultShortSupplier;
import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static com.remondis.resample.supplier.Suppliers.localDateSupplier;
import static com.remondis.resample.supplier.Suppliers.oneIntegerSupplier;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import com.remondis.resample.Sample;
import com.remondis.resample.Samples;

public class SupplierHierarchyTest {

  @Test
  public void shouldDenyNullFields() {
    assertThatThrownBy(() -> {
      Samples.of(Enums.class)
          .ignoreNullFields()
          .checkForNullFields()
          .newInstance();
    }).hasMessageContaining("The following properties were not covered by the sample generator:");
  }

  @Test
  public void shouldDefaultToDenyNullFields() {
    assertThatThrownBy(() -> {
      Samples.of(Enums.class)
          .newInstance();
    }).hasMessageContaining("The following properties were not covered by the sample generator:");
  }

  @Test
  public void shouldIgnoreNullFields() {
    Person person = Samples.of(Person.class)
        .ignoreNullFields()
        .newInstance();
    assertNull(person.getBrithday());
    assertNull(person.getName());
    assertNull(person.getForname());
    assertNotNull(person.getAge());
  }

  @Test
  public void shouldUsePrimitiveSupplier() {
    Primitives instance = Samples.of(Primitives.class)
        .newInstance();
    assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
    assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
    assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
    assertEquals(defaultDoubleSupplier().apply(null), instance.getD(), 0d);
    assertEquals(defaultFloatSupplier().apply(null), instance.getF(), 0f);
    assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
    assertEquals((long) defaultLongSupplier().apply(null), instance.getL());
    assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
    assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
    assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
  }

  @Test
  public void shouldUseGenericEnumSupplier() {
    Enums instance = Samples.of(Enums.class)
        .useForEnum(enumValueSupplier())
        .newInstance();
    assertEquals(Enumeration.ENUM_1, instance.getEnumValue());
    assertThat(instance.getList(), hasItem(Enumeration.ENUM_1));
    assertThat(instance.getSet(), hasItem(Enumeration.ENUM_1));
  }

  @Test
  public void shouldUseTypeSpecificEnumSupplier() {
    Enums instance = Samples.of(Enums.class)
        .use(() -> Enumeration.ENUM_2)
        .forType(Enumeration.class)
        .newInstance();
    assertEquals(Enumeration.ENUM_2, instance.getEnumValue());
    assertThat(instance.getList(), hasItem(Enumeration.ENUM_2));
    assertThat(instance.getSet(), hasItem(Enumeration.ENUM_2));
  }

  @Test
  public void shouldUseFieldSpecificEnumAlongsideGlobalEnumSupplier() {
    Enums instance = Samples.of(Enums.class)
        .useForEnum(enumValueSupplier())
        .use(() -> Enumeration.ENUM_2)
        .forFieldCollection(Enums::getList)
        .newInstance();
    assertEquals(Enumeration.ENUM_1, instance.getEnumValue());
    assertThat(instance.getList(), hasItem(Enumeration.ENUM_2));
    assertThat(instance.getSet(), hasItem(Enumeration.ENUM_1));
  }

  @Test
  public void shouldSelectPrimitiveTypeSettingOverWrapperTypeSetting() {
    // Make sure the test result is not affected of the order of configuration
    {
      Primitives instance = Samples.of(Primitives.class)
          .use(() -> 99L)
          .forType(Long.class)
          .use(() -> -99L)
          .forType(long.class)
          .newInstance();

      assertEquals(-99L, instance.getL()); // Make sure the type setting was used here.
      assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
      assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
      assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
      assertEquals(defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals(defaultFloatSupplier().apply(null), instance.getF(), 0f);
      assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
      assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
      assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
      assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
    }
    // Make sure the test result is not affected of the order of configuration
    {
      Primitives instance = Samples.of(Primitives.class)
          .use(() -> -99L)
          .forType(long.class)
          .use(() -> 99L)
          .forType(Long.class)
          .newInstance();
      assertEquals(-99L, instance.getL()); // Make sure the type setting was used here.
      assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
      assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
      assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
      assertEquals(defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals(defaultFloatSupplier().apply(null), instance.getF(), 0f);
      assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
      assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
      assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
      assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
    }
  }

  @Test
  public void sampleInstancesShouldOverrideTypeSetting() {
    Sample<Person> personSamples = Samples.of(Person.class)
        .useForEnum(enumValueSupplier())
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .use(localDateSupplier(2018, 10, 29))
        .forType(LocalDate.class);
    Family family = Samples.of(Family.class)
        .use(Person::new)
        .forType(Person.class)
        .useSample(personSamples)
        .newInstance();
    List<Person> persons = family.getPersons();
    assertEquals(1, persons.size());
    Person person = persons.get(0);
    assertEquals("name", person.getName());
  }

  @Test
  public void sampleSampleSupplierShouldOverrideTypeSetting() {
    Sample<Person> personSamples = Samples.of(Person.class)
        .useForEnum(enumValueSupplier())
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .use(localDateSupplier(2018, 10, 29))
        .forType(LocalDate.class)
        .use(oneIntegerSupplier())
        .forType(int.class)
        .use(longIdSampleSupplier());

    Family family = Samples.of(Family.class)
        .use(Person::new)
        .forType(Person.class)
        .useSample(personSamples)
        .newInstance();
    List<Person> persons = family.getPersons();
    assertEquals(1, persons.size());
    Person person = persons.get(0);
    assertEquals("name", person.getName());
    assertEquals(1, person.getAge());
  }

  @Test
  public void shouldSelectFieldSettingsOverTypeSettings() {
    // Make sure the test result is not affected of the order of configuration
    {
      Primitives instance = Samples.of(Primitives.class)
          .use(() -> -98L)
          .forType(Long.class)
          .use(() -> -99L)
          .forType(long.class)
          .use(() -> 99L)
          .forField(Primitives::getL)
          .newInstance();
      assertEquals(99L, instance.getL()); // Make sure the field setting was used here.
      assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
      assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
      assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
      assertEquals(defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals(defaultFloatSupplier().apply(null), instance.getF(), 0f);
      assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
      assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
      assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
      assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
    }
    // Make sure the test result is not affected of the order of configuration
    {
      Primitives instance = Samples.of(Primitives.class)
          .use(() -> -99L)
          .forType(long.class)
          .use(() -> 99L)
          .forType(Long.class)
          .newInstance();
      assertEquals(-99L, instance.getL()); // Make sure the type setting was used here.
      assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
      assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
      assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
      assertEquals(defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals(defaultFloatSupplier().apply(null), instance.getF(), 0f);
      assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
      assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
      assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
      assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
    }
  }

}

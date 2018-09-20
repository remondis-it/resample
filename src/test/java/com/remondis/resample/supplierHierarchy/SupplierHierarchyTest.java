package com.remondis.resample.supplierHierarchy;

import static com.remondis.resample.supplier.Suppliers.defaultBooleanSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultByteSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultCharacterSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultDoubleSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultFloatSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultIntegerSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultLongSupplier;
import static com.remondis.resample.supplier.Suppliers.defaultShortSupplier;
import static com.remondis.resample.supplier.Suppliers.enumValueSupplier;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Hashtable;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.Person;
import com.remondis.resample.SampleSupplier;
import com.remondis.resample.Samples;

@RunWith(MockitoJUnitRunner.class)
public class SupplierHierarchyTest {

  @Mock
  private ApplicationContext ctx;

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
    assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
    assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
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

  /**
   * This test ensures that the default primitive value suppliers can be overridden by app ctx.
   */
  @Test
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  public void shouldOverridePrimitiveSupplierByApplicationContext() {
    Map<String, SampleSupplier> map = new Hashtable<>();
    SampleSupplier<Long> longSupplier = Mockito.mock(SampleSupplier.class);
    when(longSupplier.newInstance(any(FieldInfo.class))).thenReturn(-99L);
    when(longSupplier.getType()).thenReturn(long.class);
    map.put("long", longSupplier);
    when(ctx.getBeansOfType(SampleSupplier.class)).thenReturn(map);

    Primitives instance = Samples.of(Primitives.class)
        .useApplicationContext(ctx)
        .newInstance();

    assertEquals(-99L, instance.getL());
    assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
    assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
    assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
    assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
    assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
    assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
    assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
    assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
    assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
  }

  /**
   * This test ensures that the default primitive value suppliers can be overridden by app ctx.
   */
  @Test
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  public void shouldOverrideWithWrapperIfPrimitiveOverrideDoesNotExistsByApplicationContext() {
    Map<String, SampleSupplier> map = new Hashtable<>();
    SampleSupplier<Long> longSupplier = Mockito.mock(SampleSupplier.class);
    when(longSupplier.newInstance(any(FieldInfo.class))).thenReturn(-99L);
    when(longSupplier.getType()).thenReturn(Long.class);
    map.put("LongWrapper", longSupplier);
    when(ctx.getBeansOfType(SampleSupplier.class)).thenReturn(map);

    Primitives instance = Samples.of(Primitives.class)
        .useApplicationContext(ctx)
        .newInstance();

    assertEquals(-99L, instance.getL());
    assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
    assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
    assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
    assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
    assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
    assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
    assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
    assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
    assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
  }

  /**
   * This test ensures that the default primitive value suppliers can be overridden by app ctx.
   */
  @Test
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  public void shouldSelectPrimitiveSupplierOverWrapperSupplierFromApplicationContext() {
    Map<String, SampleSupplier> map = new Hashtable<>();
    SampleSupplier<Long> longWrapperSupplier = Mockito.mock(SampleSupplier.class);
    when(longWrapperSupplier.getType()).thenReturn(Long.class);
    map.put("LongWrapper", longWrapperSupplier);
    SampleSupplier<Long> longSupplier = Mockito.mock(SampleSupplier.class);
    when(longSupplier.newInstance(any(FieldInfo.class))).thenReturn(-99L);
    when(longSupplier.getType()).thenReturn(long.class);
    map.put("Long", longSupplier);
    when(ctx.getBeansOfType(SampleSupplier.class)).thenReturn(map);

    Primitives instance = Samples.of(Primitives.class)
        .useApplicationContext(ctx)
        .newInstance();

    verify(longWrapperSupplier, never()).newInstance(any(FieldInfo.class));

    assertEquals(-99L, instance.getL());
    assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
    assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
    assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
    assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
    assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
    assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
    assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
    assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
    assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
  }

  @Test
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  public void shouldSelectTypeSettingSuppliersOverApplicationContext() {
    Map<String, SampleSupplier> map = new Hashtable<>();

    SampleSupplier<Long> longSupplier = Mockito.mock(SampleSupplier.class);
    when(longSupplier.newInstance(any(FieldInfo.class))).thenReturn(99L);
    when(longSupplier.getType()).thenReturn(long.class);
    map.put("Long", longSupplier);

    when(ctx.getBeansOfType(SampleSupplier.class)).thenReturn(map);

    Primitives instance = Samples.of(Primitives.class)
        .useApplicationContext(ctx)
        .use(() -> -99L)
        .forType(Long.class)
        .newInstance();

    assertEquals(-99L, instance.getL()); // Make sure the type setting was used here.
    assertEquals(defaultBooleanSupplier().apply(null), instance.isBool());
    assertEquals((byte) defaultByteSupplier().apply(null), instance.getB());
    assertEquals((char) defaultCharacterSupplier().apply(null), instance.getC());
    assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
    assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
    assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
    assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
    assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
    assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
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
      assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
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
      assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
      assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
      assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
      assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
      assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
    }
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
      assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
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
      assertEquals((double) defaultDoubleSupplier().apply(null), instance.getD(), 0d);
      assertEquals((float) defaultFloatSupplier().apply(null), instance.getF(), 0f);
      assertEquals((int) defaultIntegerSupplier().apply(null), instance.getI());
      assertEquals((short) defaultShortSupplier().apply(null), instance.getS());
      assertThat(instance.getFloatList(), hasItem(defaultFloatSupplier().apply(null)));
      assertThat(instance.getFloatSet(), hasItem(defaultFloatSupplier().apply(null)));
    }
  }

}

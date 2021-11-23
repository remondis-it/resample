package com.remondis.resample.genericInstantiation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.Test;

import com.remondis.resample.FieldInfo;
import com.remondis.resample.Samples;

public class GenericSampleTest {

  @Test
  public void shouldCreateGenericType() {
    Person person = Samples.Default.of(Person.class)
        .use(this::createGenericId)
        .forType(GenericId.class)
        .use(() -> 4711l)
        .forType(Long.class)
        .newInstance();
    assertNotNull(person.getId());
    assertEquals(4711, (long) person.getId()
        .getId());
  }

  private GenericId createGenericId(FieldInfo fi) {
    ParameterizedType pt = (ParameterizedType) (fi.getProperty()
        .getReadMethod()
        .getGenericReturnType());

    Type type = pt.getActualTypeArguments()[0];

    Class testType = (Class) pt.getActualTypeArguments()[0];
    return null;
  }
}

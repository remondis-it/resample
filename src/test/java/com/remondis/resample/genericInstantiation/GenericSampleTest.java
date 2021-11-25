package com.remondis.resample.genericInstantiation;

import static com.remondis.resample.genericInstantiation.ReflectionUtil.isCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
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
    assertEquals(4711, (long) person.getGenericIds()
        .get(0)
        .getId());
  }

  private GenericId createGenericId(FieldInfo fi) {
    PropertyDescriptor pd = fi.getProperty();
    ParameterizedType pt = (ParameterizedType) (pd.getReadMethod()
        .getGenericReturnType());
    Type type = null;
    if (isCollection((Class) pt.getRawType())) {
      type = pt.getActualTypeArguments()[0];
      if (type instanceof ParameterizedType) {
        ParameterizedType ptNested = (ParameterizedType) type;
        type = ptNested.getActualTypeArguments()[0];
      }
    } else {
      type = pt.getActualTypeArguments()[0];
    }
    Class idType = (Class) type;
    Object idValue = fi.getSubtypeSupplier()
        .createSubtype(fi, idType);
    return new GenericId<Serializable>((Serializable) idValue);
  }
}

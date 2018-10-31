package com.remondis.resample;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class InvocationSensorTest {

  InvocationSensor<TestBean> sensor;
  TestBean sensorObject;

  @Before
  public void setup() {
    this.sensor = new InvocationSensor<>(TestBean.class);
    this.sensorObject = this.sensor.getSensor();
  }

  @Test
  public void shouldGetAllProperties() {
    sensorObject.getDummies();
    sensorObject.getPrimitiveLong();
    sensorObject.getString();
    sensorObject.getStrings();
    sensorObject.getWrapperBoolean();
    sensorObject.isPrimitiveBoolean();
    sensorObject.getWrapperLong();
    assertTrue(sensor.hasTrackedProperties());
    List<String> propertyNames = sensor.getTrackedPropertyNames();
    assertThat(propertyNames, is(
        asList("dummies", "primitiveLong", "string", "strings", "wrapperBoolean", "primitiveBoolean", "wrapperLong")));
  }

  @Test
  public void shouldResetCorrectly() {
    sensorObject.getDummies();
    sensorObject.getPrimitiveLong();
    sensorObject.getString();
    sensorObject.getStrings();
    sensorObject.getWrapperBoolean();
    sensorObject.isPrimitiveBoolean();
    sensorObject.getWrapperLong();
    sensor.reset();
    assertFalse(sensor.hasTrackedProperties());
    sensorObject.getWrapperLong();
    List<String> propertyNames = sensor.getTrackedPropertyNames();
    assertThat(propertyNames, is(asList("wrapperLong")));
  }

  /**
   * The sensor determines get-methods only by checking the method syntactically. So non-property getter are detected as
   * well.
   */
  @Test(expected = ReflectionException.class)
  public void shouldRejectRegularMethods() {
    sensorObject.regularMethod();
  }

  @Test(expected = ReflectionException.class)
  public void shouldNotDelegateToMethodsOveridden() {
    sensorObject.toString();
  }

  @Test
  public void shouldDelegateToMethodsFromObject() {
    sensorObject.hashCode();
    assertFalse(sensor.hasTrackedProperties());
  }

}

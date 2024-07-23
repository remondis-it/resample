package com.remondis.resample;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;

public class InvocationSensorTest {

  InvocationSensor<TestBean> sensor;
  TestBean sensorObject;

  @Before
  public void setup() {
    this.sensor = new InvocationSensor<>(TestBean.class);
    this.sensorObject = this.sensor.getSensor();
    InvocationSensor.interceptionHandlerCache.clear();
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

  @Test
  public void shouldHandleClassLoaderNull() {
    InvocationSensor<NoClassLoaderBean> invocationSensor = new InvocationSensor<>(NoClassLoaderBean.class);
    assertNotNull(invocationSensor.getSensor());
  }

  @Test
  public void shouldHandleEmptyCache() {
    InvocationSensor<Dummy> invocationSensor = new InvocationSensor<>(Dummy.class);
    assertNotNull(invocationSensor.getSensor());
    assertTrue(InvocationSensor.interceptionHandlerCache.containsKey(Dummy.class));
  }

  @Test
  public void shouldReturnDefaultValueForPrimitiveType() {
    Object result = sensorObject.getPrimitiveInt();
    assertEquals(0, result);
  }

  @Test
  public void shouldReturnNullForObjectType() {
    Object result = sensorObject.getObject();
    assertNull(result);
  }

  @Test
  public void shouldCacheThreadSafe() {

    Semaphore s1 = new Semaphore(1);
    s1.acquireUninterruptibly();

    Semaphore s2 = new Semaphore(1);
    s2.acquireUninterruptibly();

    InterceptionHandler<?> interceptionHandler = InvocationSensor.interceptionHandlerCache.get(Dummy.class);

    Thread t1 = new Thread(() -> {
      InvocationSensor<Dummy> invocationSensor = new InvocationSensor<>(Dummy.class);
      Dummy sensor = invocationSensor.getSensor();
      sensor.getField();
      s2.release();
      s1.acquireUninterruptibly();
    });
    t1.start();

    // Hier warte bis t1 mindestens getString() aufgerufen hast
    s2.acquireUninterruptibly();
    InvocationSensor<Dummy> invocationSensor = new InvocationSensor<>(Dummy.class);
    List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
    assertTrue(trackedPropertyNames.isEmpty());
    s1.release();
  }

  @Test
  public void shouldCache() {
    assertTrue(InvocationSensor.interceptionHandlerCache.isEmpty());
    InvocationSensor<Dummy> invocationSensor = new InvocationSensor<>(Dummy.class);
    assertFalse(InvocationSensor.interceptionHandlerCache.isEmpty());
    assertTrue(InvocationSensor.interceptionHandlerCache.containsKey(Dummy.class));
    assertNotNull(InvocationSensor.interceptionHandlerCache.get(Dummy.class));

    InterceptionHandler<?> interceptionHandler = InvocationSensor.interceptionHandlerCache.get(Dummy.class);

    Dummy sensor = invocationSensor.getSensor();
    sensor.getField();

    List<String> trackedPropertyNames = interceptionHandler.getTrackedPropertyNames();
    assertEquals(1, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("field"));

    sensor.getAnotherField();
    trackedPropertyNames = interceptionHandler.getTrackedPropertyNames();
    assertEquals(1, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("anotherField"));

    sensor.getField();
    sensor.getAnotherField();
    trackedPropertyNames = interceptionHandler.getTrackedPropertyNames();
    assertEquals(2, trackedPropertyNames.size());
    assertTrue(trackedPropertyNames.contains("field"));
    assertTrue(trackedPropertyNames.contains("anotherField"));

  }
}

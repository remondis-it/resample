package com.remondis.resample;

import static com.remondis.resample.ReflectionUtil.denyMultipleInteractions;
import static com.remondis.resample.ReflectionUtil.getPropertyDescriptorOrFail;
import static com.remondis.resample.ReflectionUtil.isWrapperType;
import static com.remondis.resample.ReflectionUtil.unwrap;
import static com.remondis.resample.SampleException.zeroInteractions;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * The {@link SettingBuilder} defines the second stage when defining rules for sample data generation.
 * 
 * @param <T> The type to create sample instance for.
 * @param <S> The type of field to configure.
 */
public class SettingBuilder<T, S> {

  private Function<FieldInfo, S> supplier;
  private Sample<T> resample;

  SettingBuilder(Sample<T> resample, Function<FieldInfo, S> supplier) {
    super();
    requireNonNull(supplier, "supplier must not be null.");
    this.supplier = supplier;
    this.resample = resample;
  }

  public Sample<T> forType(Class<S> type) {
    requireNonNull(type, "Type must not be null.");
    resample.addTypeSetting(supplier, type);
    if (isWrapperType(type)) {
      resample.addTypeSetting(supplier, unwrap(type));
    }
    return resample;
  }

  /**
   * Registers the specified supplier function for the field defined by a field selector.
   * 
   * @param fieldSelector The field selector.
   * @return Returns the instance of {@link Sample} for method chaining.
   */
  public Sample<T> forField(TypedSelector<S, T> fieldSelector) {
    requireNonNull(fieldSelector, "Type must not be null.");
    Class<T> sensorType = resample.getType();
    InvocationSensor<T> invocationSensor = new InvocationSensor<T>(sensorType);
    T sensor = invocationSensor.getSensor();
    fieldSelector.selectField(sensor);

    if (invocationSensor.hasTrackedProperties()) {
      // ...make sure it was exactly one property interaction
      List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
      denyMultipleInteractions(trackedPropertyNames);
      // get the property name
      String propertyName = trackedPropertyNames.get(0);
      // find the property descriptor or fail with an exception
      PropertyDescriptor pd = getPropertyDescriptorOrFail(sensorType, propertyName);
      resample.addFieldSetting(pd, supplier);
    } else {
      throw zeroInteractions();
    }
    return resample;
  }

  /**
   * Registers the specified supplier function for the field defined by a field selector. This method is for collections
   * and has the same purpose like {@link #forField(TypedSelector)}.
   * 
   * @param fieldSelector The field selector.
   * @return Returns the instance of {@link Sample} for method chaining.
   */
  public Sample<T> forFieldCollection(TypedSelector<Collection<S>, T> fieldSelector) {
    requireNonNull(fieldSelector, "Type must not be null.");
    Class<T> sensorType = resample.getType();
    InvocationSensor<T> invocationSensor = new InvocationSensor<T>(sensorType);
    T sensor = invocationSensor.getSensor();
    fieldSelector.selectField(sensor);

    if (invocationSensor.hasTrackedProperties()) {
      // ...make sure it was exactly one property interaction
      List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
      denyMultipleInteractions(trackedPropertyNames);
      // get the property name
      String propertyName = trackedPropertyNames.get(0);
      // find the property descriptor or fail with an exception
      PropertyDescriptor pd = getPropertyDescriptorOrFail(sensorType, propertyName);
      Function<FieldInfo, Collection<S>> wrapper = new CollectionSupplierWrapper<>(pd.getPropertyType(), supplier);
      resample.addFieldSetting(pd, wrapper);
    } else {
      throw zeroInteractions();
    }
    return resample;
  }

}

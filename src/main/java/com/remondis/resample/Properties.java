package com.remondis.resample;

import static com.remondis.resample.CollectionSamplingMode.USE_GETTER_AND_ADD;
import static com.remondis.resample.ReflectionUtil.isCollection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Util class to get a list of all properties of a class.
 *
 * @author schuettec
 */
class Properties {

  /**
   * Caches the {@link TypeModel} per inspected type and {@link CollectionSamplingMode} so that introspection, generic
   * type resolution and accessibility checks are only performed once per type.
   */
  private static final Map<CollectionSamplingMode, Map<Class<?>, TypeModel>> TYPE_MODEL_CACHE;

  static {
    Map<CollectionSamplingMode, Map<Class<?>, TypeModel>> cache = new EnumMap<>(CollectionSamplingMode.class);
    for (CollectionSamplingMode collectionSamplingMode : CollectionSamplingMode.values()) {
      cache.put(collectionSamplingMode, new ConcurrentHashMap<>());
    }
    TYPE_MODEL_CACHE = cache;
  }

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd The pd
   * @return Returns a readable string.
   */
  static String asStringWithType(PropertyDescriptor pd) {
    String sourceClassname = Properties.getPropertyClass(pd);
    return String.format("Property '%s' (%s) in %s", pd.getName(), pd.getPropertyType()
        .getName(), sourceClassname);
  }

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd The pd
   * @return Returns a readable string.
   */
  static String asString(PropertyDescriptor pd) {
    String sourceClassname = Properties.getPropertyClass(pd);
    return String.format("Property '%s' in %s", pd.getName(), sourceClassname);
  }

  /**
   * Returns the class declaring the property.
   *
   * @param propertyDescriptor the {@link PropertyDescriptor}
   * @return Returns the class name of the declaring class.
   */
  private static String getPropertyClass(PropertyDescriptor propertyDescriptor) {
    return propertyDescriptor.getReadMethod()
        .getDeclaringClass()
        .getName();
  }

  /**
   * Returns a {@link Set} of properties with read and write access.
   *
   * @param inspectType The type to inspect.
   * @param collectionSamplingMode {@link CollectionSamplingMode} to be used.
   * @return Returns an unmodifiable set of {@link PropertyDescriptor}s that grant read and
   *         write access.
   * @throws ReflectionException Thrown on any introspection error.
   */
  static Set<PropertyDescriptor> getProperties(Class<?> inspectType, CollectionSamplingMode collectionSamplingMode) {
    return getTypeModel(inspectType, collectionSamplingMode).getProperties();
  }

  /**
   * Returns the cached {@link TypeModel} for the specified type.
   *
   * @param inspectType The type to inspect.
   * @param collectionSamplingMode {@link CollectionSamplingMode} to be used.
   * @return Returns the {@link TypeModel} of the specified type.
   * @throws ReflectionException Thrown on any introspection error.
   */
  static TypeModel getTypeModel(Class<?> inspectType, CollectionSamplingMode collectionSamplingMode) {
    return TYPE_MODEL_CACHE.get(collectionSamplingMode)
        .computeIfAbsent(inspectType, type -> new TypeModel(type, introspectProperties(type, collectionSamplingMode)));
  }

  private static Set<PropertyDescriptor> introspectProperties(Class<?> inspectType,
      CollectionSamplingMode collectionSamplingMode) {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(inspectType);
      return Arrays.stream(beanInfo.getPropertyDescriptors())
          .filter(propertyDescriptor -> isRelevantProperty(propertyDescriptor, collectionSamplingMode))
          .collect(Collectors.toSet());
    } catch (IntrospectionException e) {
      throw new ReflectionException(String.format("Cannot introspect the type %s.", inspectType.getName()));
    }
  }

  private static boolean isRelevantProperty(PropertyDescriptor propertyDescriptor,
      CollectionSamplingMode collectionSamplingMode) {
    if (propertyDescriptor.getName()
        .equals("class")) {
      return false;
    }

    if (isCollection(propertyDescriptor.getPropertyType()) && USE_GETTER_AND_ADD.equals(collectionSamplingMode)) {
      return hasGetter(propertyDescriptor);
    }
    return hasGetter(propertyDescriptor) && hasSetter(propertyDescriptor);
  }

  private static boolean hasGetter(PropertyDescriptor pd) {
    return pd.getReadMethod() != null;
  }

  private static boolean hasSetter(PropertyDescriptor pd) {
    return pd.getWriteMethod() != null;
  }

}

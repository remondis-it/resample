package com.remondis.resample;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Util class to get a list of all properties of a class.
 *
 * @author schuettec
 */
class Properties {

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd
   *        The pd
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
   * @param pd
   *        The pd
   * @return Returns a readable string.
   */
  static String asString(PropertyDescriptor pd) {
    String sourceClassname = Properties.getPropertyClass(pd);
    return String.format("Property '%s' in %s", pd.getName(), sourceClassname);
  }

  /**
   * Returns the class declaring the property.
   *
   * @param propertyDescriptor
   *        the {@link PropertyDescriptor}
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
   * @param inspectType
   *        The type to inspect.
   * @param targetType
   *        The type of mapping target.
   * @return Returns the list of {@link PropertyDescriptor}s that grant read and
   *         write access.
   * @throws MappingException
   *         Thrown on any introspection error.
   */
  static Set<PropertyDescriptor> getProperties(Class<?> inspectType) {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(inspectType);
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      return new HashSet<>(Arrays.asList(propertyDescriptors)
          .stream()
          .filter(pd -> !pd.getName()
              .equals("class"))
          .filter(Properties::hasGetter)
          .filter(Properties::hasSetter)
          .collect(Collectors.toList()));
    } catch (IntrospectionException e) {
      throw new ReflectionException(String.format("Cannot introspect the type %s.", inspectType.getName()));
    }
  }

  private static boolean hasGetter(PropertyDescriptor pd) {
    return pd.getReadMethod() != null;
  }

  private static boolean hasSetter(PropertyDescriptor pd) {
    return pd.getWriteMethod() != null;
  }

}

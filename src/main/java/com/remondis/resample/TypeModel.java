package com.remondis.resample;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds the reflective metadata of a type that is inspected for sample data generation: the set of relevant
 * properties, their accessor methods (already made accessible) and the public default constructor. Instances are
 * created once per type and {@link CollectionSamplingMode} by {@link Properties} and reused for every generated
 * instance to avoid repeated introspection, generic type resolution and accessibility checks.
 *
 * @see Properties#getTypeModel(Class, CollectionSamplingMode)
 */
class TypeModel {

  private final Set<PropertyDescriptor> properties;
  private final Map<PropertyDescriptor, Method> readMethods;
  private final Map<PropertyDescriptor, Method> writeMethods;
  private final Constructor<?> defaultConstructor;

  TypeModel(Class<?> type, Set<PropertyDescriptor> properties) {
    this.properties = Collections.unmodifiableSet(properties);
    Map<PropertyDescriptor, Method> readMethods = new HashMap<>();
    Map<PropertyDescriptor, Method> writeMethods = new HashMap<>();
    for (PropertyDescriptor pd : properties) {
      Method readMethod = pd.getReadMethod();
      if (readMethod != null) {
        readMethods.put(pd, makeAccessible(readMethod));
      }
      Method writeMethod = pd.getWriteMethod();
      if (writeMethod != null) {
        writeMethods.put(pd, makeAccessible(writeMethod));
      }
    }
    this.readMethods = readMethods;
    this.writeMethods = writeMethods;
    this.defaultConstructor = findDefaultConstructor(type);
  }

  private static Constructor<?> findDefaultConstructor(Class<?> type) {
    try {
      Constructor<?> constructor = type.getConstructor();
      try {
        constructor.setAccessible(true);
      } catch (RuntimeException e) {
        // The constructor is public, so invocation may still succeed without the accessible flag.
      }
      return constructor;
    } catch (NoSuchMethodException | SecurityException e) {
      return null;
    }
  }

  private static Method makeAccessible(Method method) {
    try {
      method.setAccessible(true);
    } catch (RuntimeException e) {
      // Do not fail the model creation for properties that may never be accessed. If the method is invoked
      // later, public methods may still be invocable without the accessible flag.
    }
    return method;
  }

  /**
   * @return Returns an unmodifiable set of the relevant {@link PropertyDescriptor}s of the type.
   */
  Set<PropertyDescriptor> getProperties() {
    return properties;
  }

  /**
   * @return Returns the accessible read method for the specified property or <code>null</code> if the property has no
   *         read method.
   */
  Method getReadMethod(PropertyDescriptor pd) {
    Method method = readMethods.get(pd);
    if (method == null) {
      method = pd.getReadMethod();
      if (method != null) {
        makeAccessible(method);
      }
    }
    return method;
  }

  /**
   * @return Returns the accessible write method for the specified property or <code>null</code> if the property has no
   *         write method.
   */
  Method getWriteMethod(PropertyDescriptor pd) {
    Method method = writeMethods.get(pd);
    if (method == null) {
      method = pd.getWriteMethod();
      if (method != null) {
        makeAccessible(method);
      }
    }
    return method;
  }

  /**
   * @return Returns the accessible public default constructor of the type or <code>null</code> if the type does not
   *         declare one.
   */
  Constructor<?> getDefaultConstructor() {
    return defaultConstructor;
  }

}

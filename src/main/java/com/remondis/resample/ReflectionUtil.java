package com.remondis.resample;

import static com.remondis.resample.ReflectionException.multipleInteractions;
import static com.remondis.resample.ReflectionException.zeroInteractions;
import static com.remondis.resample.SampleException.notAProperty;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This is a util class that provides useful reflective methods. <b>Intended for
 * internal use only!</b>.
 *
 * @author schuettec
 */
class ReflectionUtil {

  static final String IS = "is";
  static final String GET = "get";
  static final String SET = "set";

  private static final Set<Class<?>> PRIMITIVE_TYPES;
  private static final Set<Class<?>> BUILD_IN_TYPES;
  private static final Map<Class<?>, Object> DEFAULT_VALUES;
  private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS;
  private static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVES;

  static {

    // schuettec - 08.02.2017 : According to the spec:
    // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
    Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
    map.put(boolean.class, false);
    map.put(char.class, '\0');
    map.put(byte.class, (byte) 0);
    map.put(short.class, (short) 0);
    map.put(int.class, 0);
    map.put(long.class, 0L);
    map.put(float.class, 0f);
    map.put(double.class, 0d);
    map.put(Boolean.class, false);
    map.put(Character.class, '\0');
    map.put(Byte.class, (byte) 0);
    map.put(Short.class, (short) 0);
    map.put(Integer.class, 0);
    map.put(Long.class, 0L);
    map.put(Float.class, 0f);
    map.put(Double.class, 0d);
    DEFAULT_VALUES = Collections.unmodifiableMap(map);

    PRIMITIVE_TYPES = new HashSet<>();
    PRIMITIVE_TYPES.add(boolean.class);
    PRIMITIVE_TYPES.add(char.class);
    PRIMITIVE_TYPES.add(byte.class);
    PRIMITIVE_TYPES.add(short.class);
    PRIMITIVE_TYPES.add(int.class);
    PRIMITIVE_TYPES.add(long.class);
    PRIMITIVE_TYPES.add(float.class);
    PRIMITIVE_TYPES.add(double.class);

    BUILD_IN_TYPES = new HashSet<>();
    BUILD_IN_TYPES.add(Boolean.class);
    BUILD_IN_TYPES.add(Character.class);
    BUILD_IN_TYPES.add(Byte.class);
    BUILD_IN_TYPES.add(Short.class);
    BUILD_IN_TYPES.add(Integer.class);
    BUILD_IN_TYPES.add(Long.class);
    BUILD_IN_TYPES.add(Float.class);
    BUILD_IN_TYPES.add(Double.class);
    BUILD_IN_TYPES.add(String.class);

    PRIMITIVES_TO_WRAPPERS = new Hashtable<Class<?>, Class<?>>();
    PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
    PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
    PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
    PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
    PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
    PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
    PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
    PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
    PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);

    WRAPPERS_TO_PRIMITIVES = new Hashtable<Class<?>, Class<?>>();
    WRAPPERS_TO_PRIMITIVES.put(Boolean.class, boolean.class);
    WRAPPERS_TO_PRIMITIVES.put(Byte.class, byte.class);
    WRAPPERS_TO_PRIMITIVES.put(Character.class, char.class);
    WRAPPERS_TO_PRIMITIVES.put(Double.class, double.class);
    WRAPPERS_TO_PRIMITIVES.put(Float.class, float.class);
    WRAPPERS_TO_PRIMITIVES.put(Integer.class, int.class);
    WRAPPERS_TO_PRIMITIVES.put(Long.class, long.class);
    WRAPPERS_TO_PRIMITIVES.put(Short.class, short.class);
    WRAPPERS_TO_PRIMITIVES.put(Void.class, void.class);

  }

  private static final Map<String, Class<?>> primitiveNameMap = new HashMap<>();

  static {
    primitiveNameMap.put(boolean.class.getName(), boolean.class);
    primitiveNameMap.put(byte.class.getName(), byte.class);
    primitiveNameMap.put(char.class.getName(), char.class);
    primitiveNameMap.put(short.class.getName(), short.class);
    primitiveNameMap.put(int.class.getName(), int.class);
    primitiveNameMap.put(long.class.getName(), long.class);
    primitiveNameMap.put(double.class.getName(), double.class);
    primitiveNameMap.put(float.class.getName(), float.class);
    primitiveNameMap.put(void.class.getName(), void.class);
  }

  /**
   * Checks if the specified type is a Java primitive type.
   *
   * @param type
   *        The type to check
   * @return Returns <code>true</code> if the specified type is a Java primitive
   *         type, otherwise <code>false</code> is returned.
   */
  public static boolean isPrimitive(Class<?> type) {
    return PRIMITIVE_TYPES.contains(type);
  }

  /**
   * Checks if the specified type is a Java primitive or a wrapper type.
   *
   * @param type
   *        The type to check
   * @return Returns <code>true</code> if the specified type is a Java primitive or a wrapper
   *         type, otherwise <code>false</code> is returned.
   */
  public static boolean isPrimitiveCompatible(Class<?> type) {
    return isPrimitive(type) || isWrapperType(type);
  }

  /**
   * Checks if the specified type is a Java wrapper type.
   *
   * @param type
   *        The type to check
   * @return Returns <code>true</code> if the specified type is a Java wrapper
   *         type, otherwise <code>false</code> is returned.
   */
  public static boolean isWrapperType(Class<?> type) {
    return WRAPPERS_TO_PRIMITIVES.containsKey(type);
  }

  /**
   * Checks if a {@link Collection} stores values of JDK primitive wrapper types.
   *
   * @param pd {@link PropertyDescriptor}
   * @return Returns <code>true</code> if the specified {@link PropertyDescriptor} references a property that accesses
   *         {@link Collections} of primitive type wrappers, otherwise <code>false</code> is returned.
   */
  public static boolean isPrimitiveCollection(PropertyDescriptor pd) {
    return ReflectionUtil.isCollection(pd.getPropertyType()) && isWrapperType(getCollectionType(pd));
  }

  /**
   * @return Returns the generic type of the collection property the specified {@link PropertyDescriptor} points to.
   */
  public static Class<?> getCollectionType(PropertyDescriptor pd) {
    boolean isCollection = isCollection(pd.getPropertyType());
    if (isCollection) {
      ParameterizedType pt = (ParameterizedType) (pd.getReadMethod()
          .getGenericReturnType());
      return (Class<?>) pt.getActualTypeArguments()[0];
    } else {
      throw new IllegalArgumentException("PropertyDescriptor does not describe a collection property.");
    }
  }

  /**
   * @return Returns <code>true</code> if the property the specified {@link PropertyDescriptor} points to is a
   *         collection, otherwise <code>false</code> is returned.
   */
  public static boolean isCollection(PropertyDescriptor pd) {
    return isCollection(pd.getPropertyType());
  }

  /**
   * @return Returns <code>true</code> if the type is a
   *         collection, otherwise <code>false</code> is returned.
   */
  public static boolean isCollection(Class<?> propertyType) {
    return Collection.class.isAssignableFrom(propertyType);
  }

  /**
   * Performs a field identification on the specified type using the specified {@link TypedSelector}.
   * 
   * <p>
   * When building libraries that work on a field basis, it can be useful to provide an API that lets the client select
   * a field on a class since fields cannot be represented as literals. To do this an mock-instance of the target type
   * is created and a lambda function provided by the client is applied to it. The lambda is expected to call a
   * get-method for the property/field to select. This call identifies a field in the specified class that can then be
   * used by the library for any reflective purpose.
   * </p>
   * 
   * @param type The type of Java Bean.
   * @param fieldSelector The field selector lambda performing a single get-method call to select a property.
   * @return Returns the {@link PropertyDescriptor} that was selected by the {@link TypedSelector}.
   */
  public static <S, T> PropertyDescriptor getPropertyDescriptorBySensorCall(Class<T> type,
      TypedSelector<S, T> fieldSelector) {
    requireNonNull(fieldSelector, "Type must not be null.");
    InvocationSensor<T> invocationSensor = new InvocationSensor<T>(type);
    T sensor = invocationSensor.getSensor();
    fieldSelector.selectField(sensor);

    if (invocationSensor.hasTrackedProperties()) {
      // ...make sure it was exactly one property interaction
      List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
      denyMultipleInteractions(trackedPropertyNames);
      // get the property name
      String propertyName = trackedPropertyNames.get(0);
      // find the property descriptor or fail with an exception
      return getPropertyDescriptorOrFail(type, propertyName);
    } else {
      throw zeroInteractions();
    }
  }

  static void denyMultipleInteractions(List<String> trackedPropertyNames) {
    if (trackedPropertyNames.size() > 1) {
      throw multipleInteractions(trackedPropertyNames);
    }
  }

  /**
   * Ensures that the specified property name is a property in the specified
   * {@link Set} of {@link PropertyDescriptor}s.
   *
   * @param target
   *        Defines if the properties are validated against source or target
   *        rules.
   * @param type
   *        The inspected type.
   * @param propertyName
   *        The property name
   */
  static PropertyDescriptor getPropertyDescriptorOrFail(Class<?> type, String propertyName) {
    Optional<PropertyDescriptor> property;
    property = Properties.getProperties(type)
        .stream()
        .filter(pd -> pd.getName()
            .equals(propertyName))
        .findFirst();
    if (property.isPresent()) {
      return property.get();
    } else {
      throw notAProperty(type, propertyName);
    }

  }

  /**
   * Returns the default value for the specified primitive type according to the
   * Java Language Specification. See
   * https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html for
   * more information.
   *
   * @param type
   *        The type of the primitive.
   * @return Returns the default value of the specified primitive type.
   */
  @SuppressWarnings("unchecked")
  static <T> T defaultValue(Class<T> type) {
    if (DEFAULT_VALUES.containsKey(type)) {
      return (T) DEFAULT_VALUES.get(type);
    } else {
      throw new IllegalArgumentException(
          String.format("Type '%s' is not a primitive or wrapper-type.", type.getName()));
    }
  }

  /**
   * This method selects a {@link Collector} according to the specified
   * {@link Collection} instance. This method currently supports {@link Set} and
   * {@link List}.
   *
   * @param collection
   *        The actual collection instance.
   * @return Returns the {@link Collector} that creates a new {@link Collection}
   *         of the same type.
   */
  @SuppressWarnings("rawtypes")
  static Collector getCollector(Class<?> collectionType) {
    if (collectionType.isAssignableFrom(Set.class)) {
      return Collectors.toSet();
    } else if (collectionType.isAssignableFrom(List.class)) {
      return Collectors.toList();
    } else {
      throw ReflectionException.unsupportedCollectionType(collectionType);
    }
  }

  /**
   * Checks if the method has a return type.
   *
   * @param method
   *        the method
   * @return <code>true</code>, if return type is not {@link Void} or
   *         <code>false</code> otherwise.
   */
  static boolean hasReturnType(Method method) {
    return !method.getReturnType()
        .equals(Void.TYPE);
  }

  static boolean isGetterOrSetter(Method method) {
    return isGetter(method) || isSetter(method);
  }

  static boolean isSetter(Method method) {
    boolean validName = method.getName()
        .startsWith(SET);
    boolean hasArguments = hasArguments(method, 1);
    boolean hasReturnType = hasReturnType(method);
    return validName && !hasReturnType && hasArguments;
  }

  static boolean isGetter(Method method) {
    boolean isBool = isBoolGetter(method);
    boolean validName = (isBool ? method.getName()
        .startsWith(IS)
        : method.getName()
            .startsWith(GET));
    boolean hasArguments = hasArguments(method);
    boolean hasReturnType = hasReturnType(method);
    return validName && hasReturnType && !hasArguments;
  }

  static boolean isBoolGetter(Method method) {
    return isBool(method.getReturnType());
  }

  static boolean isBool(Class<?> type) {
    // isBool is used to determine if "is"-method should be used. This is only the
    // case for primitive type.
    return type == Boolean.TYPE;
  }

  static boolean hasArguments(Method method) {
    return method.getParameterCount() != 0;
  }

  static boolean hasArguments(Method method, int count) {
    return method.getParameterCount() == count;
  }

  /**
   * Returns the name of a property represented with either a getter or setter
   * method.
   *
   * @param method
   *        The getter or setter method.
   * @return Returns the name of the property.
   */
  static String toPropertyName(Method method) {
    String name = method.getName();
    if (isBoolGetter(method)) {
      return firstCharacterToLowerCase(name.substring(2, name.length()));
    } else {
      if (isGetterOrSetter(method)) {
        return firstCharacterToLowerCase(name.substring(3, name.length()));
      } else {
        throw new IllegalArgumentException("The specified method is neither a getter nor a setter method.");
      }
    }
  }

  private static String firstCharacterToLowerCase(String input) {
    char[] c = input.toCharArray();
    c[0] = Character.toLowerCase(c[0]);
    return new String(c);
  }

  /**
   * This method calls a method on the specified object. <b>This method takes into
   * account, that the specified object can also be a proxy instance.</b> In this
   * case, the method to be called must be redefined with searching it on the
   * proxy. (Proxy instances are not classes of the type the method was declared
   * in.)
   *
   * @param method
   *        The method to be invoked
   * @param targetObject
   *        The target object or proxy instance.
   * @param args
   *        (Optional) Arguments to pass to the invoked method or
   *        <code>null</code> indicating no parameters.
   * @return Returns the return value of the method on demand.
   * @throws IllegalAccessException
   *         Thrown on any access error.
   * @throws InvocationTargetException
   *         Thrown on any invocation error.
   * @throws SecurityException
   *         Thrown if the reflective operation is not allowed
   * @throws NoSuchMethodException
   *         Thrown if the proxy instance does not provide the desired method.
   */
  static Object invokeMethodProxySafe(Method method, Object targetObject, Object... args)
      throws IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
    Method effectiveMethod = method;
    Class<?> clazz = targetObject.getClass();
    if (Proxy.isProxyClass(clazz)) {
      // schuettec - 08.02.2017 : Find the method on the specified proxy.
      effectiveMethod = targetObject.getClass()
          .getMethod(method.getName(), method.getParameterTypes());
    }
    if (args == null) {
      return effectiveMethod.invoke(targetObject);
    } else {
      return effectiveMethod.invoke(targetObject, args);
    }
  }

  /**
   * Creates a new instance of the specified type.
   *
   * @param type
   *        The type to instantiate
   * @return Returns a new instance.
   */
  static <D> D newInstance(Class<D> type) {
    try {
      Constructor<D> constructor = type.getConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (InstantiationException e) {
      throw ReflectionException.noDefaultConstructor(type, e);
    } catch (Exception e) {
      throw ReflectionException.newInstanceFailed(type, e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> wrap(Class<T> c) {
    return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> unwrap(Class<T> c) {
    return isWrapperType(c) ? (Class<T>) WRAPPERS_TO_PRIMITIVES.get(c) : c;
  }

}

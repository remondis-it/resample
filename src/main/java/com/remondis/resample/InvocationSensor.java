package com.remondis.resample;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.remondis.resample.ReflectionUtil.defaultValue;
import static com.remondis.resample.ReflectionUtil.invokeMethodProxySafe;
import static com.remondis.resample.ReflectionUtil.toPropertyName;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.util.Objects.isNull;
import static net.bytebuddy.implementation.InvocationHandlerAdapter.of;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isGetter;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * The {@link InvocationSensor} tracks get-method invocations on a proxy class
 * and makes the invocation information available to the Mapper.
 *
 * @author schuettec
 */
class InvocationSensor<T> {

  static Map<Class<?>, InterceptionHandler<?>> interceptionHandlerCache = new ConcurrentHashMap<>();

  private InterceptionHandler<T> interceptionHandler;

  InvocationSensor(Class<T> superType) {
    ClassLoader classLoader;
    if (isNull(superType) || isNull(superType.getClassLoader())) {
      classLoader = getSystemClassLoader();
    } else {
      classLoader = superType.getClassLoader();
    }
    if (interceptionHandlerCache.containsKey(superType)) {
      this.interceptionHandler = (InterceptionHandler<T>) interceptionHandlerCache.get(superType);
    } else {
      Class<? extends T> proxyClass = new ByteBuddy().subclass(superType)
              .method(isGetter())
              .intercept(of((proxy, method, args) -> markPropertyAsCalled(method)))
              .method(isDeclaredBy(Object.class))
              .intercept(of((proxy, method, args) -> invokeMethodProxySafe(method, this, args)))
              .method(not(isGetter()).and(not(isDeclaredBy(Object.class))))
              .intercept(of((proxy, method, args) -> {
                throw ReflectionException.notAGetter(method);
              }))
              .make()
              .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
              .getLoaded();
      this.interceptionHandler = new InterceptionHandler<>();
      this.interceptionHandler.setProxyObject(superType.cast(ReflectionUtil.newInstance(proxyClass)));
      interceptionHandlerCache.put(superType, this.interceptionHandler);
    }
  }

  private Object markPropertyAsCalled(Method method) {
    String propertyName = toPropertyName(method);
    interceptionHandler.getThreadLocalPropertyNames().add(propertyName);
    return nullOrDefaultValue(method.getReturnType());
  }

  /**
   * Returns the proxy object get-method calls can be performed on.
   *
   * @return The proxy.
   */
  T getSensor() {
    return interceptionHandler.getProxyObject();
  }

  /**
   * Returns the list of property names that were tracked by get calls.
   *
   * @return Returns the tracked property names.
   */
  List<String> getTrackedPropertyNames() {
      return interceptionHandler.getTrackedPropertyNames();
  }

  /**
   * Checks if there were any properties accessed by get calls.
   *
   * @return Returns <code>true</code> if there were at least one interaction with a property. Otherwise
   *         <code>false</code> is returned.
   */
  boolean hasTrackedProperties() {
      return interceptionHandler.hasTrackedProperties();
  }


  void reset() {
    interceptionHandler.reset();
  }


  private static Object nullOrDefaultValue(Class<?> returnType) {
    if (returnType.isPrimitive()) {
      return defaultValue(returnType);
    } else {
      return null;
    }
  }
}

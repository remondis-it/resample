package com.remondis.resample;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.remondis.resample.ReflectionUtil.defaultValue;
import static com.remondis.resample.ReflectionUtil.invokeMethodProxySafe;
import static com.remondis.resample.ReflectionUtil.toPropertyName;
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

    private T proxyObject;

    private List<String> propertyNames = new LinkedList<>();

    InvocationSensor(Class<T> superType) {
        Class<? extends T> proxyClass = new ByteBuddy().subclass(superType)
                                                       .method(isGetter())
                                                       .intercept(of((proxy, method, args) -> markPropertyAsCalled(
                                                               method)))
                                                       .method(isDeclaredBy(Object.class))
                                                       .intercept(of((proxy, method, args) -> invokeMethodProxySafe(
                                                               method,
                                                               this,
                                                               args)))
                                                       .method(not(isGetter()).and(not(isDeclaredBy(Object.class))))
                                                       .intercept(of((proxy, method, args) -> {
                                                           throw ReflectionException.notAGetter(method);
                                                       }))
                                                       .make()
                                                       .load(getClass().getClassLoader(),
                                                             ClassLoadingStrategy.Default.INJECTION)
                                                       .getLoaded();

        proxyObject = superType.cast(ReflectionUtil.newInstance(proxyClass));
    }

    private Object markPropertyAsCalled(Method method) {
        String propertyName = toPropertyName(method);
        propertyNames.add(propertyName);
        return nullOrDefaultValue(method.getReturnType());
    }

    /**
     * Returns the proxy object get-method calls can be performed on.
     *
     * @return The proxy.
     */
    T getSensor() {
        return proxyObject;
    }

    /**
     * Returns the list of property names that were tracked by get calls.
     *
     * @return Returns the tracked property names.
     */
    List<String> getTrackedPropertyNames() {
        return Collections.unmodifiableList(propertyNames);
    }

    /**
     * Checks if there were any properties accessed by get calls.
     *
     * @return Returns <code>true</code> if there were at least one interaction with
     * a property. Otherwise <code>false</code> is returned.
     */
    boolean hasTrackedProperties() {
        return !propertyNames.isEmpty();
    }

    /**
     * Resets all tracked information.
     */
    void reset() {
        propertyNames.clear();
    }

    private static Object nullOrDefaultValue(Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return defaultValue(returnType);
        } else {
            return null;
        }
    }
}

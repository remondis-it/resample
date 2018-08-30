package com.remondis.resample;

import static com.remondis.resample.Properties.asString;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * Thrown if the mapping configuration has errors or if an actual mapping fails.
 */
public class ReflectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	ReflectionException() {
		super();
	}

	ReflectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	ReflectionException(String message) {
		super(message);
	}

	ReflectionException(Throwable cause) {
		super(cause);
	}

	static ReflectionException invocationFailed(PropertyDescriptor property, Exception e) {
		return new ReflectionException(String.format("Invoking access method for property %s failed.", property), e);
	}

	static ReflectionException invocationTarget(PropertyDescriptor property, InvocationTargetException e) {
		// Try to get the cause, because the wrapping InvocationTargetException should
		// not appear in stack trace.
		Throwable cause = e.getCause();
		if (cause == null) {
			// If the cause is null, set InvocationTargetException as cause.
			cause = e;
		}
		return new ReflectionException(
		    String.format("An access method for property %s threw an exception.", asString(property)), e);
	}

	static ReflectionException zeroInteractions(String configurationMethod) {
		return new ReflectionException(String
		    .format("The field selector for method '%s' tracked zero interactions with properties.", configurationMethod));
	}

	static ReflectionException multipleInteractions(String configurationMethod, List<String> trackedPropertyNames) {
		return new ReflectionException(String.format(
		    "The field selector for method '%s' tracked multiple interactions with the following properties: %s."
		        + " Only one interaction perfield selector is allowed!",
		    configurationMethod, String.join(",", trackedPropertyNames)));
	}

	static ReflectionException notAProperty(Class<?> type, String property) {
		return new ReflectionException(String.format(
		    "The get-method for property '%s' in type %s is not a valid Java Bean property.", property, type.getName()));
	}

	static ReflectionException notAGetter(Method method) {
		return new ReflectionException(
		    String.format("The method '%s' in type %s is not a valid Java Bean property get-method.", method.getName(),
		        method.getDeclaringClass()
		            .getName()));
	}

	static ReflectionException noReturnTypeOnGetter(Method method) {
		return new ReflectionException(
		    String.format("The method '%s' in type '%s' is not a valid getter because it has no return type.",
		        method.getName(), method.getDeclaringClass()
		            .getName()));
	}

	static ReflectionException noDefaultConstructor(Class<?> type) {
		return new ReflectionException(String.format(
		    "The type %s does not have a public no-args constructor and cannot be used for mapping.", type.getName()));
	}

	static ReflectionException noDefaultConstructor(Class<?> type, Exception e) {
		return new ReflectionException(String.format(
		    "The type %s does not have a public no-args constructor and cannot be used for mapping.", type.getName()), e);
	}

	static ReflectionException newInstanceFailed(Class<?> type, Exception e) {
		return new ReflectionException(String.format("Creating a new instance of type %s failed.", type.getName()), e);
	}

	static ReflectionException unsupportedCollection(Collection<?> collection) {
		return new ReflectionException(String.format(
		    "The collection '%s' is currently not supported. Only java.util.Set and java.util.List"
		        + " are supported collections.",
		    collection.getClass()
		        .getName()));
	}

}

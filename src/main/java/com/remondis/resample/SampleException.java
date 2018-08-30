package com.remondis.resample;

import java.util.List;

public class SampleException extends RuntimeException {

	protected SampleException() {
		super();
	}

	protected SampleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	protected SampleException(String message, Throwable cause) {
		super(message, cause);
	}

	protected SampleException(String message) {
		super(message);
	}

	protected SampleException(Throwable cause) {
		super(cause);
	}

	static SampleException zeroInteractions() {
		return new SampleException(String.format("The field selector tracked zero interactions with properties."));
	}

	static SampleException multipleInteractions(List<String> trackedPropertyNames) {
		return new SampleException(String.format(
				"The field selector tracked multiple interactions with the following properties: %s."
						+ " Only one interaction perfield selector is allowed!",
				String.join(",", trackedPropertyNames)));
	}

	static SampleException notAProperty(Class<?> type, String property) {
		return new SampleException(
				String.format("The get-method for property '%s' in type %s is not a valid Java Bean property.",
						property, type.getName()));
	}

	static SampleException valueSupplierException(Throwable e) {
		return new SampleException("A value supplier function threw an exception.", e);
	}

}

package com.remondis.resample;

import static com.remondis.resample.SampleException.multipleInteractions;
import static com.remondis.resample.SampleException.notAProperty;
import static com.remondis.resample.SampleException.zeroInteractions;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class SettingBuilder<T, S> {

	private Function<FieldInfo, S> supplier;
	private Sample<T> resample;

	SettingBuilder(Sample<T> resample, Function<FieldInfo, S> supplier) {
		super();
		this.supplier = supplier;
		this.resample = resample;
	}

	public Sample<T> forType(Class<? super S> type) {
		requireNonNull(type, "Type may not be null.");
		resample.addTypeSetting(supplier, type);
		return resample;
	}

	public Sample<T> forField(TypedSelector<S, T> fieldSelector) {
		requireNonNull(fieldSelector, "Type may not be null.");
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
	 *            Defines if the properties are validated against source or target
	 *            rules.
	 * @param type
	 *            The inspected type.
	 * @param propertyName
	 *            The property name
	 */
	static PropertyDescriptor getPropertyDescriptorOrFail(Class<?> type, String propertyName) {
		Optional<PropertyDescriptor> property;
		property = Properties.getProperties(type).stream().filter(pd -> pd.getName().equals(propertyName)).findFirst();
		if (property.isPresent()) {
			return property.get();
		} else {
			throw notAProperty(type, propertyName);
		}

	}

}

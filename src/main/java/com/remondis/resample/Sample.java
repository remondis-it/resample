package com.remondis.resample;

import static com.remondis.resample.ReflectionUtil.defaultValue;
import static com.remondis.resample.ReflectionUtil.isPrimitive;
import static com.remondis.resample.SampleException.valueSupplierException;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

/**
 * The {@link Sample} class can generate instances of beans containing sample
 * data. You can configure data factories per field. Note that the type to
 * generate sample instances for must be a Java Bean:
 *
 * <ul>
 * <li>A property is a field with any visibility</li>
 * <li>A property has a public getter/setter pair exactly named as the
 * field</li>
 * <li>Boolean values have is/setter methods.</li>
 * <li>A bean has a default zero-args constructor.</li>
 * </ul>
 *
 * @param <T>
 */
public class Sample<T> implements SampleSupplier<T>, Supplier<T> {
	public static <T> Sample<T> of(Class<T> type) {
		return new Sample<>(type);
	}

	private Class<T> type;

	private Map<Class<?>, Function<FieldInfo, ?>> typeSettings = new Hashtable<>();

	private Map<PropertyDescriptor, Function<FieldInfo, ?>> fieldSettings = new Hashtable<>();

	private boolean checkForNullFields = true;

	private ApplicationContext context;
	private Hashtable<Class<?>, SampleSupplier<?>> appCtxProviders;

	public Sample(Class<T> type) {
		super();
		this.type = type;
	}

	private boolean hasApplicationContext() {
		return nonNull(context);
	}

	@SuppressWarnings("rawtypes")
	public Sample<T> useApplicationContext(ApplicationContext context) {
		requireNonNull(context, "Application context must not be null!");
		Map<String, SampleSupplier> beansOfType = context.getBeansOfType(SampleSupplier.class);
		this.appCtxProviders = new Hashtable<Class<?>, SampleSupplier<?>>();
		beansOfType.entrySet().stream().forEach(entry -> {
			SampleSupplier supplier = entry.getValue();
			appCtxProviders.putIfAbsent(supplier.getType(), supplier);
		});
		this.context = context;
		return this;
	}

	public <S> Sample<T> useSample(Sample<S> sample) {
		use((Supplier<S>) sample).forType(sample.getType());
		return this;
	}

	public <S> SettingBuilder<T, S> use(Supplier<S> supplier) {
		return use(fieldInfo -> {
			return supplier.get();
		});
	}

	public <S> SettingBuilder<T, S> use(Function<FieldInfo, S> function) {
		requireNonNull(function, "Function may not be null.");
		return new SettingBuilder<T, S>(this, function);
	}

	public Sample<T> checkForNullFields() {
		this.checkForNullFields = true;
		return this;
	}

	public Sample<T> ignoreNullFields() {
		this.checkForNullFields = false;
		return this;
	}

	<S> void addTypeSetting(Function<FieldInfo, S> supplier, Class<? super S> type) {
		if (isPrimitive(type)) {
			throw new IllegalArgumentException(
					"Type settings are not allowed for primitive types. Please specify primitive types on fields.");
		}
		this.typeSettings.put(type, supplier);
	}

	void addFieldSetting(PropertyDescriptor propertyDescriptor, Function<FieldInfo, ?> supplier) {
		fieldSettings.put(propertyDescriptor, supplier);
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public T get() {
		return newInstance();
	}

	@Override
	public T newInstance(FieldInfo fieldInfo) {
		return get();
	}

	public T newInstance() {
		try {
			Constructor<T> constructor = type.getConstructor();
			T newInstance = constructor.newInstance();
			// Set all primitive properties
			Set<PropertyDescriptor> hitProperties = setAllValuesForPrimitiveFields(newInstance);
			// Execute all value providers available in Application Context
			Set<PropertyDescriptor> hitByAppContext = setAllValuesFromApplicationContextExcludingFieldSettings(
					newInstance);
			// Execute all type registered factories but skip the properties in the set of
			// field configurations.
			Set<PropertyDescriptor> hitByType = setAllValuesFromTypeSettingsExcludingFieldSettings(newInstance);
			// Execute all the fieldConfigurations
			Set<PropertyDescriptor> hitByField = setAllValuesFromFieldSettings(newInstance);
			hitProperties.addAll(hitByAppContext);
			hitProperties.addAll(hitByType);
			hitProperties.addAll(hitByField);
			denyNullFieldsOnDemand(hitProperties);
			return newInstance;
		} catch (SampleException e) {
			throw e;
		} catch (Exception e) {
			throw ReflectionException.newInstanceFailed(type, e);
		}
	}

	private void denyNullFieldsOnDemand(Set<PropertyDescriptor> hitProperties) {
		if (checkForNullFields) {
			Set<PropertyDescriptor> properties = Properties.getProperties(type);
			properties.removeAll(hitProperties);
			if (!properties.isEmpty()) {
				String message = properties.stream().map(PropertyDescriptor::getName)
						.collect(() -> new StringBuilder(
								"The following properties were not covered by the sample generator:\nFor class '")
										.append(type.getName()).append("'\n"),
								(acc, str) -> acc.append("- ").append(str).append("\n"),
								(sb1, sb2) -> sb1.append(sb2.toString()))
						.toString();
				throw new SampleException(message);
			}
		}
	}

	private Set<PropertyDescriptor> setAllValuesForPrimitiveFields(T newInstance) {
		return Properties.getProperties(type).stream().filter(pd -> {
			return isPrimitive(pd.getPropertyType());
		}).map(pd -> {
			setValueFromPrimitive(newInstance, pd);
			return pd;
		}).collect(Collectors.toSet());
	}

	private Set<PropertyDescriptor> setAllValuesFromFieldSettings(T newInstance) {
		return fieldSettings.entrySet().stream().map(e -> {
			PropertyDescriptor pd = e.getKey();
			setValueFromFieldSetting(newInstance, pd);
			return pd;
		}).collect(Collectors.toSet());
	}

	private Set<PropertyDescriptor> setAllValuesFromApplicationContextExcludingFieldSettings(T newInstance) {
		if (hasApplicationContext()) {
			return Properties.getProperties(type).stream().filter(pd -> {
				return !fieldSettings.containsKey(pd);
			}).filter(pd -> appCtxProviders.containsKey(pd.getPropertyType())).map(pd -> {
				setValueFromApplicationContext(newInstance, pd);
				return pd;
			}).collect(Collectors.toSet());
		} else {
			return Collections.emptySet();
		}
	}

	private Set<PropertyDescriptor> setAllValuesFromTypeSettingsExcludingFieldSettings(T newInstance) {
		return Properties.getProperties(type).stream().filter(pd -> {
			return !fieldSettings.containsKey(pd);
		}).filter(pd -> typeSettings.containsKey(pd.getPropertyType())).map(pd -> {
			setValueFromTypeSetting(newInstance, pd);
			return pd;
		}).collect(Collectors.toSet());
	}

	private void setValueFromPrimitive(T newInstance, PropertyDescriptor pd) {
		writeOrFail(pd, newInstance, defaultValue(pd.getPropertyType()));
	}

	private void setValueFromApplicationContext(T newInstance, PropertyDescriptor pd) {
		Class<?> propertyType = pd.getPropertyType();
		SampleSupplier<?> supplier = appCtxProviders.get(propertyType);
		setValue(pd, newInstance, supplier::newInstance);
	}

	private void setValueFromTypeSetting(T newInstance, PropertyDescriptor pd) {
		Class<?> propertyType = pd.getPropertyType();
		Function<FieldInfo, ?> supplier = typeSettings.get(propertyType);
		setValue(pd, newInstance, supplier);
	}

	private void setValueFromFieldSetting(T newInstance, PropertyDescriptor pd) {
		Function<FieldInfo, ?> supplier = fieldSettings.get(pd);
		setValue(pd, newInstance, supplier);
	}

	private void setValue(PropertyDescriptor pd, T newInstance, Function<FieldInfo, ?> supplier) {
		FieldInfo fieldInfo = new FieldInfo(pd.getName(), pd.getPropertyType());
		Object value;
		try {
			value = supplier.apply(fieldInfo);
		} catch (Throwable e) {
			throw valueSupplierException(e);
		}
		writeOrFail(pd, newInstance, value);
	}

	void writeOrFail(PropertyDescriptor property, Object targetInstance, Object value) {
		try {
			Method writeMethod = property.getWriteMethod();
			writeMethod.setAccessible(true);
			writeMethod.invoke(targetInstance, value);
		} catch (InvocationTargetException e) {
			throw ReflectionException.invocationTarget(property, e);
		} catch (Exception e) {
			throw ReflectionException.invocationFailed(property, e);
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Creating samples of '").append(type.getName()).append("'\n");
		fieldSettings.entrySet().stream().forEach(e -> {
			b.append("- applying value factory for field '").append(e.getKey().getReadMethod().getName()).append("'\n");
		});
		typeSettings.entrySet().stream().forEach(e -> {
			b.append("- applying value factory producing ").append(e.getKey().getName()).append("\n");
		});
		return b.toString();
	}

}

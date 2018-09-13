package com.remondis.resample;

import static com.remondis.resample.ReflectionUtil.defaultValue;
import static com.remondis.resample.ReflectionUtil.getCollectionType;
import static com.remondis.resample.ReflectionUtil.getCollector;
import static com.remondis.resample.ReflectionUtil.isCollection;
import static com.remondis.resample.ReflectionUtil.isPrimitive;
import static com.remondis.resample.ReflectionUtil.isPrimitiveCollection;
import static com.remondis.resample.SampleException.valueSupplierException;
import static java.util.Arrays.asList;
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

  private Function<FieldInfo, Enum<?>> enumValueSupplier;

  private boolean useAutoSampling;

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
    beansOfType.entrySet()
        .stream()
        .forEach(entry -> {
          SampleSupplier supplier = entry.getValue();
          appCtxProviders.putIfAbsent(supplier.getType(), supplier);
        });
    this.context = context;
    return this;
  }

  public Sample<T> useAutoSampling() {
    this.useAutoSampling = true;
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

  public Sample<T> useForEnum(Function<FieldInfo, Enum<?>> function) {
    this.enumValueSupplier = function;
    return this;
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
      T newInstance = createNewInstance(type);
      // Set all primitive properties
      Set<PropertyDescriptor> hitProperties = setAllValuesForPrimitiveFields(newInstance);
      // Set all enum values
      Set<PropertyDescriptor> hitEnums = setAllEnumValues(newInstance);
      // Execute all value providers available in Application Context
      Set<PropertyDescriptor> hitByAppContext = setAllValuesByApplicationContextExcludingFieldSettings(newInstance);
      // Execute all type registered factories but skip the properties in the set of
      // field configurations.
      Set<PropertyDescriptor> hitByType = setAllValuesByTypeSettingsExcludingFieldSettings(newInstance);
      // Execute all the fieldConfigurations
      Set<PropertyDescriptor> hitByField = setAllValuesByFieldSettings(newInstance);
      hitProperties.addAll(hitByAppContext);
      hitProperties.addAll(hitEnums);
      hitProperties.addAll(hitByType);
      hitProperties.addAll(hitByField);

      Set<PropertyDescriptor> hitByAutoSampling = setAllValuesByAutoSampling(hitProperties, newInstance);
      hitProperties.addAll(hitByAutoSampling);

      denyNullFieldsOnDemand(hitProperties);
      return newInstance;
    } catch (SampleException e) {
      throw e;
    } catch (Exception e) {
      throw ReflectionException.newInstanceFailed(type, e);
    }
  }

  private Set<PropertyDescriptor> setAllValuesByAutoSampling(Set<PropertyDescriptor> hitProperties, T newInstance) {
    if (useAutoSampling) {
      Set<PropertyDescriptor> notHitFields = getNotHitFields(hitProperties);
      Set<PropertyDescriptor> hitFields = notHitFields.stream()
          .filter(pd -> setValueByAutoSampling(pd, newInstance))
          .collect(Collectors.toSet());
      return hitFields;
    } else {
      return Collections.emptySet();
    }
  }

  private boolean setValueByAutoSampling(PropertyDescriptor pd, T newInstance) {
    Class<?> type = null;
    if (isCollection(pd)) {
      type = getCollectionType(pd);
    } else {
      type = pd.getPropertyType();
    }

    Sample<?> autoSample = Sample.of(type);
    autoSample.typeSettings = new Hashtable<>(this.typeSettings);
    autoSample.checkForNullFields = this.checkForNullFields;
    autoSample.context = this.context;
    if (nonNull(appCtxProviders)) {
      autoSample.appCtxProviders = new Hashtable(this.appCtxProviders);
    }
    autoSample.enumValueSupplier = this.enumValueSupplier;
    autoSample.useAutoSampling = this.useAutoSampling;

    Function<FieldInfo, ?> supplier = null;
    if (isCollection(pd)) {
      supplier = wrapInList(pd, autoSample::newInstance);
    } else {
      supplier = autoSample::newInstance;
    }

    try {
      setValue(pd, newInstance, supplier);
    } catch (Exception e) {
      throw SampleException.autoSamplingFailed(pd, autoSample, e);
    }
    return true;

  }

  private static <T> T createNewInstance(Class<T> type)
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Constructor<T> constructor = type.getConstructor();
    T newInstance = constructor.newInstance();
    return newInstance;
  }

  private Set<PropertyDescriptor> setAllEnumValues(T newInstance) {
    if (nonNull(enumValueSupplier)) {
      return Properties.getProperties(type)
          .stream()
          .filter(pd -> {
            Class<?> propertyType = pd.getPropertyType();
            return propertyType.isEnum() || (isCollection(propertyType) && getCollectionType(pd).isEnum());
          })
          .filter(pd -> {
            return !fieldSettings.containsKey(pd);
          })
          .filter(pd -> {
            return !fieldSettings.containsKey(pd);
          })
          .map(pd -> {
            if (isCollection(pd.getPropertyType())) {
              setValue(pd, newInstance, wrapInList(pd, enumValueSupplier));
            } else {
              setValue(pd, newInstance, enumValueSupplier);
            }
            return pd;
          })
          .collect(Collectors.toSet());
    } else {
      return Collections.emptySet();
    }
  }

  private Function<FieldInfo, ?> wrapInList(PropertyDescriptor pd, Function<FieldInfo, ?> supplier) {
    return (fi) -> {
      Class<?> collectionType = getCollectionType(pd);
      return asList(supplier.apply(new FieldInfo(pd.getName(), collectionType))).stream()
          .collect(getCollector(pd.getPropertyType()));

    };
  }

  private void denyNullFieldsOnDemand(Set<PropertyDescriptor> hitProperties) {
    if (checkForNullFields) {
      Set<PropertyDescriptor> properties = getNotHitFields(hitProperties);
      if (!properties.isEmpty()) {
        String message = properties.stream()
            .map(PropertyDescriptor::getName)
            .collect(
                () -> new StringBuilder(
                    "The following properties were not covered by the sample generator:\nFor class '")
                        .append(type.getName())
                        .append("'\n"),
                (acc, str) -> acc.append("- ")
                    .append(str)
                    .append("\n"),
                (sb1, sb2) -> sb1.append(sb2.toString()))
            .toString();
        throw new SampleException(message);
      }
    }
  }

  private Set<PropertyDescriptor> getNotHitFields(Set<PropertyDescriptor> hitProperties) {
    Set<PropertyDescriptor> properties = Properties.getProperties(type);
    properties.removeAll(hitProperties);
    return properties;
  }

  private Set<PropertyDescriptor> setAllValuesForPrimitiveFields(T newInstance) {
    return Properties.getProperties(type)
        .stream()
        .filter(pd -> {
          return isPrimitive(pd.getPropertyType()) || isPrimitiveCollection(pd);
        })
        .map(pd -> {
          Class<?> propertyType = pd.getPropertyType();
          if (isPrimitiveCollection(pd)) {
            Class<?> collectionType = getCollectionType(pd);
            @SuppressWarnings("unchecked")
            Object valueToSet = asList(defaultValue(collectionType)).stream()
                .collect(getCollector(propertyType));
            writeOrFail(pd, newInstance, valueToSet);
          } else {
            writeOrFail(pd, newInstance, defaultValue(propertyType));
          }
          return pd;
        })
        .collect(Collectors.toSet());
  }

  private Set<PropertyDescriptor> setAllValuesByFieldSettings(T newInstance) {
    return fieldSettings.entrySet()
        .stream()
        .map(e -> {
          PropertyDescriptor pd = e.getKey();
          setValueFromFieldSetting(newInstance, pd);
          return pd;
        })
        .collect(Collectors.toSet());
  }

  private Set<PropertyDescriptor> setAllValuesByApplicationContextExcludingFieldSettings(T newInstance) {
    if (hasApplicationContext()) {
      return Properties.getProperties(type)
          .stream()
          .filter(pd -> {
            return !fieldSettings.containsKey(pd);
          })
          .filter(pd -> {
            if (isCollection(pd)) {
              return appCtxProviders.containsKey(getCollectionType(pd));
            } else {
              return appCtxProviders.containsKey(pd.getPropertyType());
            }
          })
          .map(pd -> {
            setValueFromApplicationContext(newInstance, pd);
            return pd;
          })
          .collect(Collectors.toSet());
    } else {
      return Collections.emptySet();
    }
  }

  private Set<PropertyDescriptor> setAllValuesByTypeSettingsExcludingFieldSettings(T newInstance) {
    return Properties.getProperties(type)
        .stream()
        .filter(pd -> {
          return !fieldSettings.containsKey(pd);
        })
        .filter(pd -> {
          if (isCollection(pd)) {
            return typeSettings.containsKey(getCollectionType(pd));
          } else {
            return typeSettings.containsKey(pd.getPropertyType());
          }
        })
        .map(pd -> {
          setValueFromTypeSetting(newInstance, pd);
          return pd;
        })
        .collect(Collectors.toSet());
  }

  private void setValueFromApplicationContext(T newInstance, PropertyDescriptor pd) {
    if (isCollection(pd)) {
      Class<?> propertyType = getCollectionType(pd);
      SampleSupplier<?> supplier = appCtxProviders.get(propertyType);
      setValue(pd, newInstance, wrapInList(pd, supplier::newInstance));
    } else {
      Class<?> propertyType = pd.getPropertyType();
      SampleSupplier<?> supplier = appCtxProviders.get(propertyType);
      setValue(pd, newInstance, supplier::newInstance);
    }
  }

  private void setValueFromTypeSetting(T newInstance, PropertyDescriptor pd) {
    if (isCollection(pd)) {
      Class<?> propertyType = getCollectionType(pd);
      Function<FieldInfo, ?> supplier = typeSettings.get(propertyType);
      setValue(pd, newInstance, wrapInList(pd, supplier));
    } else {
      Class<?> propertyType = pd.getPropertyType();
      Function<FieldInfo, ?> supplier = typeSettings.get(propertyType);
      setValue(pd, newInstance, supplier);
    }

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
    StringBuilder b = new StringBuilder("Creating samples of '").append(type.getName())
        .append("'\n");
    fieldSettings.entrySet()
        .stream()
        .forEach(e -> {
          b.append("- applying value factory for field '")
              .append(e.getKey()
                  .getReadMethod()
                  .getName())
              .append("'\n");
        });
    typeSettings.entrySet()
        .stream()
        .forEach(e -> {
          b.append("- applying value factory producing ")
              .append(e.getKey()
                  .getName())
              .append("\n");
        });
    return b.toString();
  }

}

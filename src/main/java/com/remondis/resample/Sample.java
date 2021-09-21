package com.remondis.resample;

import static com.remondis.resample.ReflectionUtil.defaultValue;
import static com.remondis.resample.ReflectionUtil.getCollectionType;
import static com.remondis.resample.ReflectionUtil.getCollector;
import static com.remondis.resample.ReflectionUtil.isCollection;
import static com.remondis.resample.ReflectionUtil.isPrimitiveCollection;
import static com.remondis.resample.ReflectionUtil.isPrimitiveCompatible;
import static com.remondis.resample.SampleException.valueSupplierException;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The {@link Sample} class can generate instances of beans containing sample
 * data. You can configure data factories per type or per field. Note that the
 * type to generate sample instances for must be a Java Bean:
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
public final class Sample<T> implements Supplier<T> {
  private Class<T> type;

  private Map<Class<?>, Function<FieldInfo, ?>> typeSettings = new Hashtable<>();

  private Map<PropertyDescriptor, Function<FieldInfo, ?>> fieldSettings = new Hashtable<>();

  private boolean checkForNullFields = true;

  private Function<FieldInfo, Enum<?>> enumValueSupplier;

  private boolean useAutoSampling;

  private CollectionSamplingMode collectionSamplingMode = CollectionSamplingMode.USE_SETTER_METHODE;

  Sample(Class<T> type) {
    super();
    this.type = type;
  }

  /**
   * Configures the {@link Sample} instance to use auto-sampling. The
   * generator will then try to generate all transitive object references by
   * using the same instance of {@link Sample}.
   *
   * @return Returns this object for method chaining.
   */
  public Sample<T> useAutoSampling() {
    this.useAutoSampling = true;
    return this;
  }

  /**
   * Configures the {@link Sample} instance to <b>deactivate</b>
   * auto-sampling.
   *
   * @return Returns this object for method chaining.
   */
  public Sample<T> deactivateAutoSampling() {
    this.useAutoSampling = false;
    return this;
  }

  /**
   * Configures the {@link Sample} instance to use the given <b>collectionSamplingMode</b>.
   * Default is {@link CollectionSamplingMode#USE_SETTER_METHODE}
   *
   * @return Returns this object for method chaining.
   */
  public Sample<T> collectionSamplingMode(CollectionSamplingMode collectionSamplingMode) {
    Objects.requireNonNull(collectionSamplingMode, "CollectionSamplingMode must not be null.");
    this.collectionSamplingMode = collectionSamplingMode;
    return this;
  }

  /**
   * Configures the specified {@link Sample} instance to be used by this
   * {@link Sample}. The specified sampler will be used by type.
   *
   * @return Returns {@link SettingBuilder} to specify the scope of this
   *         supplier.
   */
  public <S> Sample<T> useSample(Sample<S> sample) {
    use((Supplier<S>) sample).forType(sample.getType());
    return this;
  }

  /**
   * Configures the {@link Supplier} to be used by this {@link Sample}
   * instance. The scope in which this supplier is used is defined on the
   * object that is returned by this method.
   *
   * @return Returns {@link SettingBuilder} to specify the scope of this
   *         supplier.
   */
  public <S> SettingBuilder<T, S> use(Supplier<S> supplier) {
    return use(fieldInfo -> {
      return supplier.get();
    });
  }

  /**
   * Configures the {@link SampleSupplier} to be used by this {@link Sample}
   * instance.
   * <p>
   * This method does the same as: <br/>
   * <code>this.use(sampleSupplier::newInstance).forType(sampleSupplier.getType());</code>
   * </p>
   *
   * @return Returns {@link SettingBuilder} to specify the scope of this
   *         supplier.
   */
  public <S> Sample<T> use(SampleSupplier<S> sampleSupplier) {
    this.use(sampleSupplier::newInstance)
        .forType(sampleSupplier.getType());
    return this;
  }

  /**
   * Configures the {@link Function} to be used by this {@link Sample}
   * instance. The scope in which this function is used is defined on the
   * object that is returned by this method.
   *
   * <p>
   * This method enables supplier implementations to get more information of
   * the target field.
   * </p>
   *
   * @return Returns {@link SettingBuilder} to specify the scope of this
   *         supplier.
   */
  public <S> SettingBuilder<T, S> use(Function<FieldInfo, S> function) {
    requireNonNull(function, "Function must not be null.");
    return new SettingBuilder<T, S>(this, function);
  }

  /**
   * Configures a {@link Function} to be used as enum value supplier function.
   *
   * @return Returns {@link SettingBuilder} to specify the scope of this
   *         supplier.
   */
  public Sample<T> useForEnum(Function<FieldInfo, Enum<?>> function) {
    this.enumValueSupplier = function;
    return this;
  }

  /**
   * Activates a check to ensure that all Bean properties got a sample
   * instance value after generation.
   *
   * @return Returns this object for method chaining.
   */
  public Sample<T> checkForNullFields() {
    this.checkForNullFields = true;
    return this;
  }

  /**
   * Deactivates the instance check: It is possible to skip fields in the
   * sample configuration so no values will be generated for them.
   *
   * @return Returns this object for method chaining.
   */
  public Sample<T> ignoreNullFields() {
    this.checkForNullFields = false;
    return this;
  }

  <S> void addTypeSetting(Function<FieldInfo, S> supplier, Class<? super S> type) {
    this.typeSettings.put(type, supplier);
  }

  void addFieldSetting(PropertyDescriptor propertyDescriptor, Function<FieldInfo, ?> supplier) {
    fieldSettings.put(propertyDescriptor, supplier);
  }

  public Class<T> getType() {
    return this.type;
  }

  public CollectionSamplingMode getCollectionSamplingMode() {
    return this.collectionSamplingMode;
  }

  /**
   * @return Generates and returns a new instance of the specified type.
   */
  public T newInstance() {
    try {
      T newInstance = createNewInstance(type);
      // Set all primitive properties
      Set<PropertyDescriptor> hitProperties = setAllValuesForPrimitiveFields(newInstance);
      // Set all enum values
      Set<PropertyDescriptor> hitEnums = setAllEnumValues(newInstance);
      // Execute all type registered factories but skip the properties in
      // the set of
      // field configurations.
      Set<PropertyDescriptor> hitByType = setAllValuesByTypeSettingsExcludingFieldSettings(newInstance);
      // Execute all the fieldConfigurations
      Set<PropertyDescriptor> hitByField = setAllValuesByFieldSettings(newInstance);

      hitProperties.addAll(hitEnums);
      hitProperties.addAll(hitByType);
      hitProperties.addAll(hitByField);

      // Sample all non-hit Maps
      Set<PropertyDescriptor> hitByMaps = setAllValuesByMapSampling(hitProperties, newInstance);
      hitProperties.addAll(hitByMaps);

      // Autosample non-hit fields
      Set<PropertyDescriptor> hitByAutoSampling = setAllValuesByAutoSampling(hitProperties, newInstance);
      hitProperties.addAll(hitByAutoSampling);

      denyNullFieldsOnDemand(hitProperties);
      return newInstance;
    } catch (SampleException e) {
      throw e;
    } catch (AutoSamplingException e) {
      throw e;
    } catch (Exception e) {
      throw ReflectionException.newInstanceFailed(type, e);
    }
  }

  private Set<PropertyDescriptor> setAllValuesByMapSampling(Set<PropertyDescriptor> hitProperties, T newInstance) {
    Set<PropertyDescriptor> notHitFields = getNotHitFields(hitProperties);
    Set<PropertyDescriptor> hitFields = notHitFields.stream()
        .filter(pd -> Map.class.isAssignableFrom(pd.getPropertyType()))
        .filter(pd -> setValueByMapSampling(pd, newInstance))
        .collect(Collectors.toSet());
    return hitFields;
  }

  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  private boolean setValueByMapSampling(PropertyDescriptor pd, T newInstance) {

    ParameterizedType pt = (ParameterizedType) pd.getReadMethod()
        .getGenericReturnType();
    Type[] actualTypeArguments = pt.getActualTypeArguments();

    Map newMap = new Hashtable();
    Class<?> keyType = (Class<?>) actualTypeArguments[0];
    Class<?> valueType = (Class<?>) actualTypeArguments[1];
    Object key = sampleMapItem(pd, keyType);
    Object value = sampleMapItem(pd, valueType);

    if (nonNull(key) && nonNull(value)) {
      newMap.put(key, value);
      setValue(pd, newInstance, fi -> newMap);
      return true;
    } else {
      return false;
    }
  }

  private Object sampleMapItem(PropertyDescriptor pd, Class<?> type) {
    FieldInfo fi = new FieldInfo(pd, type);
    if (type.isEnum()) {
      if (nonNull(enumValueSupplier)) {
        return enumValueSupplier.apply(fi);
      } else {
        return null;
      }
    } else {
      if (typeSettings.containsKey(type)) {
        Function<FieldInfo, ?> supplier = typeSettings.get(type);
        return supplier.apply(fi);
      } else if (useAutoSampling) {
        @SuppressWarnings("rawtypes")
        Sample autoSampleSupplier = createAutoSampling(pd, type);
        return autoSampleSupplier.newInstance();
      } else {
        return null;
      }
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

    Sample<?> autoSample = createAutoSampling(pd, type);

    Function<FieldInfo, ?> supplier = null;
    if (isCollection(pd)) {
      supplier = wrapInList(pd, fi -> autoSample.newInstance());
    } else {
      supplier = fi -> autoSample.newInstance();
    }

    try {
      setValue(pd, newInstance, supplier);
    } catch (AutoSamplingException e) {
      throw e;
    } catch (Exception e) {
      throw AutoSamplingException.autoSamplingFailed(pd, e);
    }
    return true;
  }

  private <S> Sample<S> createAutoSampling(PropertyDescriptor pd, Class<S> type) {
    try {
      denyNoDefaultConstructor(type);
    } catch (Exception e) {
      throw AutoSamplingException.autoSamplingFailed(pd, e);
    }

    Sample<S> autoSample = Samples.of(type);
    autoSample.typeSettings = new Hashtable<>(this.typeSettings);
    autoSample.checkForNullFields = this.checkForNullFields;
    autoSample.enumValueSupplier = this.enumValueSupplier;
    autoSample.useAutoSampling = this.useAutoSampling;
    autoSample.collectionSamplingMode = this.collectionSamplingMode;

    return autoSample;
  }

  private void denyNoDefaultConstructor(Class<?> type) {
    try {
      type.getConstructor();
    } catch (NoSuchMethodException | SecurityException e) {
      throw SampleException.noDefaultConstructor(type);
    }
  }

  private static <T> T createNewInstance(Class<T> type)
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Constructor<T> constructor = type.getConstructor();
    constructor.setAccessible(true);
    T newInstance = constructor.newInstance();
    return newInstance;
  }

  private Set<PropertyDescriptor> setAllEnumValues(T newInstance) {
    if (nonNull(enumValueSupplier)) {
      return Properties.getProperties(type, collectionSamplingMode)
          .stream()
          .filter(pd -> {
            Class<?> propertyType = pd.getPropertyType();
            return propertyType.isEnum() || (isCollection(propertyType) && getCollectionType(pd).isEnum());
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

  @SuppressWarnings("unchecked")
  private Function<FieldInfo, ?> wrapInList(PropertyDescriptor pd, Function<FieldInfo, ?> supplier) {
    return (fi) -> {
      Class<?> collectionType = getCollectionType(pd);
      return asList(supplier.apply(new FieldInfo(pd, collectionType))).stream()
          .collect(getCollector(pd.getPropertyType()));

    };
  }

  private void denyNullFieldsOnDemand(Set<PropertyDescriptor> hitProperties) {
    if (checkForNullFields) {
      Set<PropertyDescriptor> properties = getNotHitFields(hitProperties);
      if (!properties.isEmpty()) {
        String message = properties.stream()
            // .map(PropertyDescriptor::getName)
            .collect(
                () -> new StringBuilder(
                    "The following properties were not covered by the sample generator:\nFor class '")
                        .append(type.getName())
                        .append("'\n"),
                (acc, pd) -> acc.append("- ")
                    .append(pd.getName())
                    .append(", accessed by ")
                    .append(pd.getPropertyType()
                        .getName())
                    .append(" ")
                    .append(pd.getReadMethod()
                        .getName())
                    .append("()")
                    .append(", class: ")
                    .append(pd.getReadMethod()
                        .getDeclaringClass())
                    .append("\n"),
                (sb1, sb2) -> sb1.append(sb2.toString()))
            .toString();
        throw new SampleException(message);
      }
    }
  }

  private Set<PropertyDescriptor> getNotHitFields(Set<PropertyDescriptor> hitProperties) {
    Set<PropertyDescriptor> properties = Properties.getProperties(type, collectionSamplingMode);
    properties.removeAll(hitProperties);
    return properties;
  }

  private Set<PropertyDescriptor> setAllValuesForPrimitiveFields(T newInstance) {
    return Properties.getProperties(type, collectionSamplingMode)
        .stream()
        .filter(pd -> {
          return isPrimitiveCompatible(pd.getPropertyType()) || isPrimitiveCollection(pd);
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

  private Set<PropertyDescriptor> setAllValuesByTypeSettingsExcludingFieldSettings(T newInstance) {
    return Properties.getProperties(type, collectionSamplingMode)
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
    FieldInfo fieldInfo = new FieldInfo(pd, pd.getPropertyType());
    Object value;
    try {
      value = supplier.apply(fieldInfo);
    } catch (AutoSamplingException e) {
      throw e;
    } catch (Throwable e) {
      throw valueSupplierException(e);
    }

    writeOrFail(pd, newInstance, value);
  }

  void writeOrFail(PropertyDescriptor property, Object targetInstance, Object value) {
    try {
      if (isCollection(property) && CollectionSamplingMode.USE_GETTER_AND_ADD.equals(collectionSamplingMode)) {
        Method readMethod = property.getReadMethod();
        readMethod.setAccessible(true);
        Collection<Object> invoke = (Collection<Object>) readMethod.invoke(targetInstance);
        if (value instanceof Collection) {
          invoke.addAll((Collection) value);
        } else {
          invoke.add(value);
        }
      } else {
        Method writeMethod = property.getWriteMethod();
        writeMethod.setAccessible(true);
        writeMethod.invoke(targetInstance, value);
      }
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

  boolean hasTypeSetting(Class<?> type) {
    return typeSettings.containsKey(type);
  }

  @Override
  public T get() {
    return newInstance();
  }

}

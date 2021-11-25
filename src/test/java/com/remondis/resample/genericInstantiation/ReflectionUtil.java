package com.remondis.resample.genericInstantiation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * This is a util class that provides useful reflective methods. <b>Intended for
 * internal use only!</b>.
 *
 * @author schuettec
 */
class ReflectionUtil {

  /**
   * @return Returns the generic type of the collection property the specified {@link PropertyDescriptor} points to.
   */
  public static Class<?> getCollectionType(PropertyDescriptor pd) {
    boolean isCollection = isCollection(pd.getPropertyType());
    if (isCollection) {
      ParameterizedType pt = (ParameterizedType) (pd.getReadMethod()
          .getGenericReturnType());
      Type type = pt.getActualTypeArguments()[0];
      if (type instanceof ParameterizedType) {
        type = ((ParameterizedType) type).getRawType();
      }
      return (Class<?>) type;
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

}

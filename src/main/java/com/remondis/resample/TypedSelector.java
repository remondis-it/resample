package com.remondis.resample;

/**
 * A {@link TypedSelector} can be used when a field should be identified on a target object. The typed selector is a
 * function, that is applied to the destination object. Implementors should perform a specific get-method call on the
 * destination object to identify a field.
 * 
 * <p>
 * This interface is used to specify lambdas for field selection.
 * </p>
 *
 * @param <T>
 *        The object type selecting a field on.
 * @param <R>
 *        The type of the field.
 * @author schuettec
 */
@FunctionalInterface
public interface TypedSelector<R, T> {

  /**
   * This method is used to perform a get-method invocation of the specified
   * destination object and returning its value. This invocation tells the framework
   * which property is to be selected for the following configuration and what
   * type it has.
   *
   * @param destination
   *        The destination object to perform a get-method invocation on.
   * @return Returns the return value of the performed get-method call.
   */
  R selectField(T destination);

}

package com.remondis.resample;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class InterceptionHandler<T> {

  private T proxyObject;
  private final ThreadLocal<List<String>> threadLocalPropertyNames = ThreadLocal.withInitial(LinkedList::new);

  public void setProxyObject(T proxyObject) {
    this.proxyObject = proxyObject;
  }

  public T getProxyObject() {
    return proxyObject;
  }

  public List<String> getTrackedPropertyNames() {
    List<String> list = threadLocalPropertyNames.get();
    // Reset thread local after access.
    reset();
    return isNull(list) ? Collections.emptyList() : unmodifiableList(list);
  }

  public List<String> getThreadLocalPropertyNames() {
    return threadLocalPropertyNames.get();
  }

  /**
   * Resets the thread local list of property names.
   */
  void reset() {
    threadLocalPropertyNames.remove();
  }

  public boolean hasTrackedProperties() {
    return nonNull(threadLocalPropertyNames.get()) && !threadLocalPropertyNames.get()
        .isEmpty();
  }
}

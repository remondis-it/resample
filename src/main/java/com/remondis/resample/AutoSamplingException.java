package com.remondis.resample;

import java.beans.PropertyDescriptor;

public class AutoSamplingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private AutoSamplingException(String message, Throwable cause) {
    super(message, cause);
  }

  static AutoSamplingException autoSamplingFailed(PropertyDescriptor pd, Exception e) {
    return new AutoSamplingException(String.format("Auto-sampling failed for type %s accessed by %s() in type '%s'.",
        pd.getReadMethod()
            .getReturnType()
            .getCanonicalName(),
        pd.getReadMethod()
            .getName(),
        pd.getReadMethod()
            .getDeclaringClass()
            .getName()),
        e);
  }
}

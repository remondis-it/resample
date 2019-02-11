package com.remondis.resample;

import java.beans.PropertyDescriptor;

public class AutoSamplingException extends RuntimeException {

  private AutoSamplingException(String message, Throwable cause) {
    super(message, cause);
  }

  static AutoSamplingException autoSamplingFailed(PropertyDescriptor pd, Sample<?> autoSample, Exception e) {
    return new AutoSamplingException(String.format(
        "Auto-sampling failed for property accessed by %s() in type '%s' and configuration:\n%s", pd.getReadMethod()
            .getName(),
        pd.getReadMethod()
            .getDeclaringClass()
            .getName(),
        autoSample), e);
  }
}

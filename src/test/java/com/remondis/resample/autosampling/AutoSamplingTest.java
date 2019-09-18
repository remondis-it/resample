package com.remondis.resample.autosampling;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.resample.AutoSamplingException;
import com.remondis.resample.SampleException;
import com.remondis.resample.Samples;

public class AutoSamplingTest {

  @Test
  public void shouldFailAutosampling() {
    assertThatThrownBy(() -> Samples.of(Data.class)
        .useAutoSampling()
        .get()).isInstanceOf(AutoSamplingException.class)
            .hasMessage(
                "Auto-sampling failed for type java.math.BigDecimal accessed by getId() in type 'com.remondis.resample.autosampling.Data'.");
  }

  @Test
  public void shouldDeactivateAuto() {
    assertThatThrownBy(() -> Samples.of(Data.class)
        .useAutoSampling()
        .deactivateAutoSampling()
        .get()).isInstanceOf(SampleException.class)
            .hasMessageContaining("The following properties were not covered by the sample generator:");
  }

}

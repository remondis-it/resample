package com.remondis.resample.autosampling;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.resample.SampleException;
import com.remondis.resample.Samples;

public class AutoSamplingTest {

  @Test
  public void shouldFailAutosampling() {
    assertThatThrownBy(() -> Samples.of(Data.class)
        .useAutoSampling()
        .get()).isInstanceOf(SampleException.class)
            .hasMessage(
                "Cannot create instance of type 'java.math.BigDecimal': No or not accessible default constructor.");
  }

}

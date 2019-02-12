package com.remondis.resample.maps;

import org.junit.Test;

import com.remondis.resample.Samples;

public class MapTest {

  @Test
  public void shouldSampleMap() {
    Dummy dummy = Samples.Default.of(Dummy.class)
        .get();
    System.out.println(dummy);
  }

}

package com.remondis.resample.regression;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.remondis.resample.Sample;
import com.remondis.resample.Samples;
import com.remondis.resample.supplier.Suppliers;

public class DateTimeSupplierBug {

  /**
   * The bug was a call to {@link ZonedDateTime#now()} while creating instances. The generation must be independent from
   * the system clock.
   */
  @Test
  public void test() {
    Sample<Dummy> dummySample = Samples.Default.of(Dummy.class)
        .use(Suppliers.zonedDateTimeSampleSupplier(2018, 1, 31, 12, 30, 0, 0, ZoneId.of("Europe/Berlin")));
    Dummy d1 = dummySample.newInstance();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
    Dummy d2 = dummySample.newInstance();
    assertEquals(d1.getValidFrom(), d2.getValidFrom());
    assertEquals(d1.getValidTo(), d2.getValidTo());
  }

}

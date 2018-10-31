package com.remondis.resample;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FunctionSupplierTest {

  @Mock
  private Function<FieldInfo, String> mock;

  @Test
  public void shouldApply() {
    FunctionSupplier<String> function = new FunctionSupplier<String>(String.class, mock);
    function.newInstance(null);
    verify(mock, times(1)).apply(null);
  }

}

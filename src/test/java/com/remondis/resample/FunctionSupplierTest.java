package com.remondis.resample;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FunctionSupplierTest {

  @Mock
  private Function<FieldInfo, String> mock;

  @Test
  public void shouldApply() {
    FunctionSupplier<String> function = new FunctionSupplier<String>(String.class, mock);
    function.newInstance(null);
    verify(mock, times(1)).apply(null);
  }

  @Test
  public void shouldReturnFunction() {
    FunctionSupplier<String> function = new FunctionSupplier<String>(String.class, mock);
    assertSame(mock, function.function());
  }

}

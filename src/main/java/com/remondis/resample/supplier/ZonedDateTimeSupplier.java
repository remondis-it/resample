package com.remondis.resample.supplier;

import static com.remondis.resample.supplier.Suppliers.zonedDateTimeSupplier;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.remondis.resample.FunctionSupplier;

@Component
public class ZonedDateTimeSupplier extends FunctionSupplier<ZonedDateTime> {
	public ZonedDateTimeSupplier() {
		super(ZonedDateTime.class, zonedDateTimeSupplier());
	}
}

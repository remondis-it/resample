package com.remondis.resample;

import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;
import static com.remondis.resample.supplier.Suppliers.genericEnumValueSupplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.remondis.resample.supplier.LocalDateSupplier;
import com.remondis.resample.supplier.ZonedDateTimeSupplier;

@Service
@Import({
    LocalDateSupplier.class, ZonedDateTimeSupplier.class
})
public class SampleService {

	private ApplicationContext ctx;

	@Autowired
	public SampleService(ApplicationContext ctx) {
		super();
		this.ctx = ctx;
	}

	public <T> Sample<T> of(Class<T> type) {
		return Sample.of(type)
		    .checkForNullFields()
		    .use(fieldNameStringSupplier())
		    .forType(String.class)
		    .use(genericEnumValueSupplier())
		    .forType(Enum.class)
		    .useApplicationContext(ctx);
	}

}

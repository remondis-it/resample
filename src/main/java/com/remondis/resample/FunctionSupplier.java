package com.remondis.resample;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

public class FunctionSupplier<T> extends AbstractSampleSupplier<T> {

	private Function<FieldInfo, T> supplierFunction;

	public FunctionSupplier(Class<T> type, Function<FieldInfo, T> supplierFunction) {
		super(type);
		requireNonNull(supplierFunction, "Supplier function must not be null.");
		this.supplierFunction = supplierFunction;
	}

	@Override
	public T newInstance(FieldInfo fieldInfo) {
		return supplierFunction.apply(fieldInfo);
	}
}

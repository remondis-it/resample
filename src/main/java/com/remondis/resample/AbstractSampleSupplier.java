package com.remondis.resample;

import static java.util.Objects.requireNonNull;

public abstract class AbstractSampleSupplier<T> implements SampleSupplier<T> {

	private Class<T> type;

	public AbstractSampleSupplier(Class<T> type) {
		super();
		requireNonNull(type, "Type must not be null.");
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

}

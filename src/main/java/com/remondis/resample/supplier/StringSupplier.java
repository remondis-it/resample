package com.remondis.resample.supplier;

import java.util.function.Function;
import java.util.function.Supplier;

import com.remondis.resample.FieldInfo;

public class StringSupplier {

	public static Supplier<String> emptyStringSupplier() {
		return () -> {
			return new String();
		};
	}

	public static Function<FieldInfo, String> fieldNameStringSupplier() {
		return (fi) -> {
			return new String(fi.getPropertyName());
		};
	}

}

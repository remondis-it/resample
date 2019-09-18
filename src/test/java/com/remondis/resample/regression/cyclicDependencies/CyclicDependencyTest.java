package com.remondis.resample.regression.cyclicDependencies;

import org.junit.Test;

import com.remondis.resample.Sample;
import com.remondis.resample.Samples;

public class CyclicDependencyTest {

	@Test
	public void test() {
		// A a = Samples.Default.of(A.class)
		// // .deactivateAutoSampling()
		// // .useSample(
		// // Samples.of(B.class).use(() -> null).forField(B::getA))
		// .get();

		Sample<A> aSample = Samples.Default.of(A.class);

		aSample = aSample.deactivateAutoSampling().useSample(Samples.Default
				.of(B.class).deactivateAutoSampling().useSample(aSample));

		A a = aSample.get();
		System.out.println(a);

	}

}

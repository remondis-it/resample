package com.remondis.resample.benchmark;

import static com.remondis.resample.supplier.Suppliers.fieldNameStringSupplier;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.remondis.resample.Sample;
import com.remondis.resample.Samples;

/**
 * JMH benchmark for the sample generation hot paths: plain bean generation, auto-sampling of a whole object graph and
 * the configuration API including the field selector.
 *
 * <p>
 * Run with:
 *
 * <pre>
 * mvn test-compile org.codehaus.mojo:exec-maven-plugin:3.1.0:exec \
 *     -Dexec.executable=java \
 *     -Dexec.args="-classpath %classpath com.remondis.resample.benchmark.SampleGenerationBenchmark" \
 *     -Dexec.classpathScope=test
 * </pre>
 *
 * <p>
 * Note: <code>exec:java</code> does not work here, because JMH forks a measurement JVM using the
 * <code>java.class.path</code> system property which does not contain the project classpath when running inside the
 * Maven JVM.
 * </p>
 * </p>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SampleGenerationBenchmark {

  private Sample<BenchmarkAddress> flatBeanSample;
  private Sample<BenchmarkPerson> objectGraphSample;

  @Setup
  public void setup() {
    this.flatBeanSample = Samples.of(BenchmarkAddress.class)
        .checkForNullFields()
        .use(fieldNameStringSupplier())
        .forType(String.class);
    this.objectGraphSample = Samples.Default.of(BenchmarkPerson.class);
  }

  /**
   * Measures the generation of a flat bean with a pre-configured {@link Sample}.
   */
  @Benchmark
  public BenchmarkAddress flatBean() {
    return flatBeanSample.newInstance();
  }

  /**
   * Measures the generation of an object graph with transitive object references, collections and maps using
   * auto-sampling.
   */
  @Benchmark
  public BenchmarkPerson objectGraphWithAutoSampling() {
    return objectGraphSample.newInstance();
  }

  /**
   * Measures the configuration API including the field selector plus the generation of a flat bean, as typically used
   * per test case.
   */
  @Benchmark
  public BenchmarkAddress configureAndCreateFlatBean() {
    return Samples.of(BenchmarkAddress.class)
        .checkForNullFields()
        .use(fieldNameStringSupplier())
        .forType(String.class)
        .use(() -> 12345)
        .forField(BenchmarkAddress::getZipCode)
        .newInstance();
  }

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder().include(SampleGenerationBenchmark.class.getSimpleName())
        .build();
    new Runner(options).run();
  }

}

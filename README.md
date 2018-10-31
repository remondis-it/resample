# ReSample - A Test Fixture Generator

# Table of Contents
1. Overview
2. Features

## Overview

ReSample is a small library that creates sample instances of Java Beans by filling in data from configurable factories. This simplifies creation of test fixtures to be used in unit tests. Fixtures can be used to apply the ObjectMother-Pattern (https://martinfowler.com/bliki/ObjectMother.html).

This library reduces the overhead of creating fixtures by initializing the complete object with valid (non-null) values. This way you can focus on the data needed for the test case rather than setting all required values by hand to not break not-null constraints for example.

### Example

The following example demonstrates how to generate a sample instance of `Person`.

```
	class Person {
		private String name;
		private String forname;
		private int age;
		private LocalDate brithday;

		// Default Constructor
		// Getter / Setter...
	}

	@Test
	public void shouldGenerateSampleData() {
		Person person = Sample.of(Person.class)
		    .checkForNullFields()
		    .use(fieldNameStringSupplier())
		    .forType(String.class)
		    .use(localDateSupplier())
		    .forField(Person::getBrithday)
		    .newInstance();
		System.out.println(person);
	}
```

The above example will generate a `Person` instance with the follwing values:
```
Person [name=name, forname=forname, age=0, brithday=2018-08-30]
```

## Features

ReSample provides the following features to create test data fixtures:
- Primitive types are initialized with default values out-of-the-box.
- Generation can be customized with value factories
  - Supported are supplier- or function-lambdas
- Factories can be registered for
  - types
  - fields
  - global (only for enumeration value suppliers)
- Factories can be obtained by
  - configuration builder
  - other Sample-instances
- Auto-sampling: Applies a configuration recursively to reduce configuration overhead.
- Ready to use suppliers are shipped with this library
  - `LocalDate` supplier
  - `Date` supplier
  - `ZonedDateTime` supplier
  - `TimeZone` supplier
  - Generic enumeration supplier
  - String suppliers
  - Default value suppliers for primitive or wrapper types
  - even more...

## How to use

```
	@Test
	public void shouldGenerateSampleData() {
		Person person = Sample.of(Person.class)
		    .checkForNullFields()
		    .use(fieldNameStringSupplier())
		    .forType(String.class)
		    .use(localDateSupplier())
		    .forField(Person::getBrithday)
		    .newInstance();
		System.out.println(person);
	}
```

The example above shows how to use the core functions of ReSample. Basically you can register supplier lambdas on a per type or per field basis. The fixture generator then creates a new instance of the specified type reflectively and applies all supplier lambdas. The suppliers are expected to create new values each time they are called. All field hit by the configuration will be access through setter methods to set the generated values.

ReSample can ensure that every property of the Java Bean was hit by a configuration. This way the generation aborts with an exception if the object is not completely initialized after data generation. This way it is easy to get notified if a field was added to the Java Bean but no sample value would be generated.

### Scope hierarchy

ReSample defines a scope hierarchy that is used when applying value factories:

1. all primitive fields and lists (of respective wrapper types) are filled with default values.
2. all enumeration fields and lists are filled using the global enumeration value supplier
4. all fields and lists are filled using the suppliers registered for types
5. all configured fields are filled using the specified supplier

### Auto-sampling

ReSample supports auto-sampling which tries to reduce the configuration overhead for large object graphs. When auto-sampling is active the configuration is applied to all transitive references of the object graph. This way all Java Beans can be generated using one configuration.


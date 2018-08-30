# [PROOF-OF-CONCEPT] Sample Data Generator

# Table of Contents
1. Overview

## Overview

This is a small library that creates sample instances of objects by filling in data from configurable factories.

### Example

The following example demonstrates how to generate a sample instance of `Person`.

```
	static class Person {
		private String name;
		private String forname;
		private int age;
		private LocalDate brithday;

		public Person() {
			super();
		}

		public Person(String name, String forname, int age, LocalDate brithday) {
			super();
			this.name = name;
			this.forname = forname;
			this.age = age;
			this.brithday = brithday;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getForname() {
			return forname;
		}

		public void setForname(String forname) {
			this.forname = forname;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public LocalDate getBrithday() {
			return brithday;
		}

		public void setBrithday(LocalDate brithday) {
			this.brithday = brithday;
		}

		@Override
		public String toString() {
			return "Person [name=" + name + ", forname=" + forname + ", age=" + age + ", brithday=" + brithday + "]";
		}
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
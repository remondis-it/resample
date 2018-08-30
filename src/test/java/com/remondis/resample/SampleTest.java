package com.remondis.resample;

import static com.remondis.resample.supplier.DateSupplier.localDateSupplier;
import static com.remondis.resample.supplier.DateSupplier.zonedDateTimeSupplier;
import static com.remondis.resample.supplier.StringSupplier.fieldNameStringSupplier;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.remondis.example.Address;
import com.remondis.example.Facility;
import com.remondis.example.ForeignOperatorInfo;
import com.remondis.example.Plant;

public class SampleTest {

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

	@Test
	public void shouldGeneratePlant() {
		Sample<Address> adressSampler = Sample.of(Address.class)
		    .checkForNullFields()
		    .use(fieldNameStringSupplier())
		    .forType(String.class);

		Sample<ForeignOperatorInfo> foi = Sample.of(ForeignOperatorInfo.class)
		    .checkForNullFields()
		    .useSample(adressSampler)
		    .use(fieldNameStringSupplier())
		    .forType(String.class);

		Sample<Facility> facilitySampler = Sample.of(Facility.class)
		    .checkForNullFields()
		    .use(fieldNameStringSupplier())
		    .forType(String.class)
		    .useSample(adressSampler);

		Sample<Plant> plantSample = Sample.of(Plant.class)
		    .checkForNullFields()
		    .use(fieldNameStringSupplier())
		    .forType(String.class)
		    .use(zonedDateTimeSupplier())
		    .forType(ZonedDateTime.class)
		    .useSample(foi)
		    .useSample(facilitySampler);

		System.out.println(plantSample);
		System.out.println(plantSample.newInstance());

	}
}

package com.remondis.resample;

import static com.remondis.resample.supplier.StringSupplier.fieldNameStringSupplier;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.remondis.example.Address;
import com.remondis.example.Facility;
import com.remondis.example.ForeignOperatorInfo;
import com.remondis.example.Plant;

@RunWith(MockitoJUnitRunner.class)
public class SampleTest {

	private static final LocalDate LOCALDATE = LocalDate.of(2018, 12, 12);

	private static final String STRING = "string";

	@Mock
	private Supplier<String> stringSupplier;

	@Mock
	private Supplier<LocalDate> localDateSupplier;

	@Test
	public void shouldDenyPrimitiveTypeSetting() {
		assertThatThrownBy(() -> {
			Sample.of(Person.class).use(() -> 1L).forType(Long.class).newInstance();
		}).isInstanceOf(IllegalArgumentException.class).hasMessage(
				"Type settings are not allowed for primitive types. Please specify primitive types on fields.");
	}

	@Test
	public void shouldGenerateSampleData() {
		doReturn(STRING).when(stringSupplier).get();
		doReturn(LOCALDATE).when(localDateSupplier).get();
		Person person = Sample.of(Person.class).use(stringSupplier).forType(String.class).use(localDateSupplier)
				.forField(Person::getBrithday).newInstance();
		verify(stringSupplier, times(2)).get();
		verify(localDateSupplier, times(1)).get();
		assertEquals(STRING, person.getName());
		assertEquals(STRING, person.getForname());
		assertEquals(0, person.getAge());
		assertEquals(LOCALDATE, person.getBrithday());
	}

	@Test
	public void shouldIgnoreNullFields() {
		Person person = Sample.of(Person.class).ignoreNullFields().newInstance();
		assertNull(person.getBrithday());
		assertNull(person.getName());
		assertNull(person.getForname());
		assertNotNull(person.getAge());
	}

	@Test
	public void shouldDenyNullFieldsAsDefault() {
		assertThatThrownBy(() -> {
			Sample.of(Person.class).newInstance();
		}).isInstanceOf(SampleException.class)
				.hasMessageContaining("The following properties were not covered by the sample generator:");
	}

	@Test
	public void shouldDenyNullFields() {
		assertThatThrownBy(() -> {
			Sample.of(Person.class).ignoreNullFields().checkForNullFields().newInstance();
		}).isInstanceOf(SampleException.class)
				.hasMessageContaining("The following properties were not covered by the sample generator:");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void fieldSettingsShouldOverrideTypeSettings() {
		String OVERRIDDEN_STRING = "thisShouldBeOverridden";
		doReturn(OVERRIDDEN_STRING).when(stringSupplier).get();
		LocalDate OVERRIDDEN_LOCAL_DATE = LocalDate.of(1, 1, 1);
		doReturn(OVERRIDDEN_LOCAL_DATE).when(localDateSupplier).get();

		Supplier<String> stringOverridden = Mockito.mock(Supplier.class);
		Supplier<LocalDate> localDateOverridden = Mockito.mock(Supplier.class);

		doReturn(OVERRIDDEN_STRING).when(stringOverridden).get();
		doReturn(OVERRIDDEN_LOCAL_DATE).when(localDateOverridden).get();

		doReturn(STRING).when(stringSupplier).get();
		doReturn(LOCALDATE).when(localDateSupplier).get();

		Person person = Sample.of(Person.class).use(stringOverridden).forType(String.class).use(localDateOverridden)
				.forType(LocalDate.class).use(stringSupplier).forField(Person::getForname).use(stringSupplier)
				.forField(Person::getName).use(localDateSupplier).forField(Person::getBrithday).use(() -> 99)
				.forField(Person::getAge).newInstance();

		verify(stringOverridden, times(0)).get();
		verify(localDateOverridden, times(0)).get();

		verify(stringSupplier, times(2)).get();
		verify(localDateSupplier, times(1)).get();

		assertEquals(STRING, person.getName());
		assertEquals(STRING, person.getForname());
		assertEquals(99, person.getAge());
		assertEquals(LOCALDATE, person.getBrithday());
	}

	@Test
	public void shouldGeneratePlant() {

		Supplier<ZonedDateTime> zonedDateTimeSupplier = Mockito.mock(Supplier.class);
		ZonedDateTime EXPECTED_DATE = ZonedDateTime.of(2018, 8, 30, 1, 1, 1, 0, ZoneId.of("Europe/Berlin"));
		doReturn(EXPECTED_DATE).when(zonedDateTimeSupplier).get();

		Sample<Address> adressSampler = Sample.of(Address.class).checkForNullFields().use(fieldNameStringSupplier())
				.forType(String.class);

		Sample<ForeignOperatorInfo> foiSampler = Sample.of(ForeignOperatorInfo.class).checkForNullFields()
				.useSample(adressSampler).use(fieldNameStringSupplier()).forType(String.class);

		Sample<Facility> facilitySampler = Sample.of(Facility.class).checkForNullFields().use(fieldNameStringSupplier())
				.forType(String.class).useSample(adressSampler);

		Plant plant = Sample.of(Plant.class).checkForNullFields().use(fieldNameStringSupplier()).forType(String.class)
				.use(zonedDateTimeSupplier).forType(ZonedDateTime.class).useSample(foiSampler)
				.useSample(facilitySampler).newInstance();

		assertEquals(0L, (long) plant.getId());
		assertEquals(0L, (long) plant.getPlantIdPrevious());
		assertEquals(0L, (long) plant.getVersion());
		assertEquals("description", plant.getDescription());
		assertEquals(EXPECTED_DATE, plant.getValidFrom());
		assertEquals(EXPECTED_DATE, plant.getValidTo());

		ForeignOperatorInfo foi = plant.getForeignOperatorInfo();
		assertEquals(0L, (long) foi.getId());
		assertEquals("plantOwnerName", foi.getPlantOwnerName());
		Address address = foi.getAddress();

		assertEquals("street", address.getStreet());
		assertEquals("houseNumber", address.getHouseNumber());
		assertEquals("city", address.getCity());
		assertEquals("zipCode", address.getZipCode());

		assertEquals("countryCode", address.getCountryCode());

		assertEquals("openingHours", foi.getOpeningHours());

		Facility facility = plant.getLocalOperatorInfo();
		assertEquals(0L, (long) facility.getId());
		assertEquals("name", facility.getName());
		assertEquals(address, facility.getAddress());

	}
}

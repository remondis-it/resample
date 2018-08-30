package com.remondis.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

	private String street;

	private String houseNumber;

	private String city;

	private String zipCode;

	private String countryCode;

}

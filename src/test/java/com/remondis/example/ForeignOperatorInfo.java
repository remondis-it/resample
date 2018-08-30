package com.remondis.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForeignOperatorInfo {

	private Long id;

	private String plantOwnerName;

	private Address address;

	private String openingHours;

}

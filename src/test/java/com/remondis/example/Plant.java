package com.remondis.example;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plant {

	private Long id;

	private Long plantIdPrevious;

	private Long version;

	private String description;

	private ForeignOperatorInfo foreignOperatorInfo;

	private Facility localOperatorInfo;

	private ZonedDateTime validFrom;

	private ZonedDateTime validTo;

}

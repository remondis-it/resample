package com.remondis.example;

import java.time.ZonedDateTime;

public class Plant {

	private Long id;

	private Long plantIdPrevious;

	private Long version;

	private String description;

	private ForeignOperatorInfo foreignOperatorInfo;

	private Facility localOperatorInfo;

	private ZonedDateTime validFrom;

	private ZonedDateTime validTo;

	public Plant(Long id, Long plantIdPrevious, Long version, String description,
			ForeignOperatorInfo foreignOperatorInfo, Facility localOperatorInfo, ZonedDateTime validFrom,
			ZonedDateTime validTo) {
		super();
		this.id = id;
		this.plantIdPrevious = plantIdPrevious;
		this.version = version;
		this.description = description;
		this.foreignOperatorInfo = foreignOperatorInfo;
		this.localOperatorInfo = localOperatorInfo;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public Plant() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlantIdPrevious() {
		return plantIdPrevious;
	}

	public void setPlantIdPrevious(Long plantIdPrevious) {
		this.plantIdPrevious = plantIdPrevious;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ForeignOperatorInfo getForeignOperatorInfo() {
		return foreignOperatorInfo;
	}

	public void setForeignOperatorInfo(ForeignOperatorInfo foreignOperatorInfo) {
		this.foreignOperatorInfo = foreignOperatorInfo;
	}

	public Facility getLocalOperatorInfo() {
		return localOperatorInfo;
	}

	public void setLocalOperatorInfo(Facility localOperatorInfo) {
		this.localOperatorInfo = localOperatorInfo;
	}

	public ZonedDateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(ZonedDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public ZonedDateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(ZonedDateTime validTo) {
		this.validTo = validTo;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((foreignOperatorInfo == null) ? 0 : foreignOperatorInfo.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((localOperatorInfo == null) ? 0 : localOperatorInfo.hashCode());
		result = prime * result + ((plantIdPrevious == null) ? 0 : plantIdPrevious.hashCode());
		result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
		result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Plant other = (Plant) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (foreignOperatorInfo == null) {
			if (other.foreignOperatorInfo != null)
				return false;
		} else if (!foreignOperatorInfo.equals(other.foreignOperatorInfo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (localOperatorInfo == null) {
			if (other.localOperatorInfo != null)
				return false;
		} else if (!localOperatorInfo.equals(other.localOperatorInfo))
			return false;
		if (plantIdPrevious == null) {
			if (other.plantIdPrevious != null)
				return false;
		} else if (!plantIdPrevious.equals(other.plantIdPrevious))
			return false;
		if (validFrom == null) {
			if (other.validFrom != null)
				return false;
		} else if (!validFrom.equals(other.validFrom))
			return false;
		if (validTo == null) {
			if (other.validTo != null)
				return false;
		} else if (!validTo.equals(other.validTo))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}

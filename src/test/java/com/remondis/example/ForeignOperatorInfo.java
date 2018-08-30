package com.remondis.example;

public class ForeignOperatorInfo {

	private Long id;

	private String plantOwnerName;

	private Address address;

	private String openingHours;

	public ForeignOperatorInfo(Long id, String plantOwnerName, Address address, String openingHours) {
		super();
		this.id = id;
		this.plantOwnerName = plantOwnerName;
		this.address = address;
		this.openingHours = openingHours;
	}

	public ForeignOperatorInfo() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlantOwnerName() {
		return plantOwnerName;
	}

	public void setPlantOwnerName(String plantOwnerName) {
		this.plantOwnerName = plantOwnerName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((openingHours == null) ? 0 : openingHours.hashCode());
		result = prime * result + ((plantOwnerName == null) ? 0 : plantOwnerName.hashCode());
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
		ForeignOperatorInfo other = (ForeignOperatorInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (openingHours == null) {
			if (other.openingHours != null)
				return false;
		} else if (!openingHours.equals(other.openingHours))
			return false;
		if (plantOwnerName == null) {
			if (other.plantOwnerName != null)
				return false;
		} else if (!plantOwnerName.equals(other.plantOwnerName))
			return false;
		return true;
	}

}

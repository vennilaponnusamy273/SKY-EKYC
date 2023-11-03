package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentAddressDetails {
	@JsonProperty("address")
	private String address;
	@JsonProperty("locality_or_post_office")
	private String localityOrPostOffice;
	@JsonProperty("district_or_city")
	private String districtOrCity;
	@JsonProperty("state")
	private String state;
	@JsonProperty("pincode")
	private String pincode;
}

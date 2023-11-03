package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Aadhaar {
	@JsonProperty("id_number")
	private String idNumber;
	@JsonProperty("document_type")
	private String documentType;
	@JsonProperty("id_proof_type")
	private String idProofType;
	@JsonProperty("gender")
	private String gender;
	@JsonProperty("image")
	private String image;
	@JsonProperty("name")
	private String name;
	@JsonProperty("dob")
	private String dob;
	@JsonProperty("current_address")
	private String currentAddress;
	@JsonProperty("permanent_address")
	private String permanentAddress;
	@JsonProperty("current_address_details")
	private CurrentAddressDetails currentAddressDetails;
	@JsonProperty("permanent_address_details")
	private PermanentAddressDetails permanentAddressDetails;
}

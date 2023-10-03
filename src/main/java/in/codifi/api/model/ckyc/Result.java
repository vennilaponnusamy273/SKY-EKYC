package in.codifi.api.model.ckyc;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "PERSONAL_DETAILS", "IDENTITY_DETAILS", "RELATED_PERSON_DETAILS", "IMAGE_DETAILS" })
public class Result {
	@JsonProperty("PERSONAL_DETAILS")
	private PersonalDetails personalDetails;
	@JsonProperty("IDENTITY_DETAILS")
	private IdentityDetails identityDetails;
	@JsonProperty("RELATED_PERSON_DETAILS")
	private Object relatedPersonDetails;
	@JsonProperty("IMAGE_DETAILS")
	private ImageDetails imageDetails;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("PERSONAL_DETAILS")
	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	@JsonProperty("PERSONAL_DETAILS")
	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	@JsonProperty("IDENTITY_DETAILS")
	public IdentityDetails getIdentityDetails() {
		return identityDetails;
	}

	@JsonProperty("IDENTITY_DETAILS")
	public void setIdentityDetails(IdentityDetails identityDetails) {
		this.identityDetails = identityDetails;
	}

	@JsonProperty("RELATED_PERSON_DETAILS")
	public Object getRelatedPersonDetails() {
		return relatedPersonDetails;
	}

	@JsonProperty("RELATED_PERSON_DETAILS")
	public void setRelatedPersonDetails(Object relatedPersonDetails) {
		this.relatedPersonDetails = relatedPersonDetails;
	}

	@JsonProperty("IMAGE_DETAILS")
	public ImageDetails getImageDetails() {
		return imageDetails;
	}

	@JsonProperty("IMAGE_DETAILS")
	public void setImageDetails(ImageDetails imageDetails) {
		this.imageDetails = imageDetails;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}

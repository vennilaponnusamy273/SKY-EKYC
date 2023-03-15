package in.codifi.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressModel {
	@JsonProperty("Message")
	private String message;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("PostOffice")
	private List<PostOfficeModel> postOffice;

	@JsonProperty("Message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("Message")
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty("Status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("Status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("PostOffice")
	public List<PostOfficeModel> getPostOffice() {
		return postOffice;
	}

	@JsonProperty("PostOffice")
	public void setPostOffice(List<PostOfficeModel> postOffice) {
		this.postOffice = postOffice;
	}
}

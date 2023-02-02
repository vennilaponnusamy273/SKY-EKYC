package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErpExistingApiModel {

	@JsonProperty("stat")
	private String status;
	@JsonProperty("existing")
	private String existing;
	@JsonProperty("error")
	private String error;

	@JsonProperty("stat")
	public String getStatus() {
		return status;
	}

	@JsonProperty("stat")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("existing")
	public String getExisting() {
		return existing;
	}

	@JsonProperty("existing")
	public void setExisting(String existing) {
		this.existing = existing;
	}

	@JsonProperty("error")
	public String getError() {
		return error;
	}

	@JsonProperty("error")
	public void setError(String error) {
		this.error = error;
	}

}

package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocJson {
	@JsonProperty("real")
	private String real;
	@JsonProperty("spoof")
	private String spoof;

	@JsonProperty("real")
	public String getReal() {
		return real;
	}

	@JsonProperty("real")
	public void setReal(String real) {
		this.real = real;
	}

	@JsonProperty("spoof")
	public String getSpoof() {
		return spoof;
	}

	@JsonProperty("spoof")
	public void setSpoof(String spoof) {
		this.spoof = spoof;
	}
}

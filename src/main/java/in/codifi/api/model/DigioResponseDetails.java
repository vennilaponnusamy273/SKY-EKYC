package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioResponseDetails {
	@JsonProperty("aadhaar")
	private Aadhaar aadhaar;
}

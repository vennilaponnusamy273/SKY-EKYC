package in.codifi.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookDigioPayloadModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("digilocker_request")
	private WebhookDigilockerRequestModel digilockerRequest;

}
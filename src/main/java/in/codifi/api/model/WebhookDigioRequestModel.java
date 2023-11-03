package in.codifi.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookDigioRequestModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("entities")
	private List<String> entities;
	@JsonProperty("payload")
	private WebhookDigioPayloadModel payload;
	@JsonProperty("id")
	private String id;
	@JsonProperty("created_at")
	private Long createdAt;
	@JsonProperty("event")
	private String event;
}

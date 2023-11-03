package in.codifi.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioResponseAccessToken implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("created_at")
	private String createdAt;
	@JsonProperty("id")
	private String id;
	@JsonProperty("entity_id")
	private String entityId;
	@JsonProperty("valid_till")
	private String validTill;
}

package in.codifi.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioIniResponseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("id")
	private String id;
	@JsonProperty("created_at")
	private String createdAt;
	@JsonProperty("status")
	private String status;
	@JsonProperty("customer_identifier")
	private String customerIdentifier;
	@JsonProperty("reference_id")
	private String referenceId;
	@JsonProperty("transaction_id")
	private String transactionId;
	@JsonProperty("customer_name")
	private String customerName;
	@JsonProperty("expire_in_days")
	private Integer expireInDays;
	@JsonProperty("reminder_registered")
	private Boolean reminderRegistered;
	@JsonProperty("access_token")
	private DigioResponseAccessToken accessToken;
	@JsonProperty("auto_approved")
	private Boolean autoApproved;
}

package in.codifi.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioSaveAddResponse {
	@JsonProperty("id")
	private String id;
	@JsonProperty("updated_at")
	private String updatedAt;
	@JsonProperty("created_at")
	private String createdAt;
	@JsonProperty("status")
	private String status;
	@JsonProperty("customer_identifier")
	private String customerIdentifier;
	@JsonProperty("actions")
	private List<DigioResponseAction> actions;
	@JsonProperty("reference_id")
	private String referenceId;
	@JsonProperty("transaction_id")
	private String transactionId;
	@JsonProperty("expire_in_days")
	private Integer expireInDays;
	@JsonProperty("reminder_registered")
	private Boolean reminderRegistered;
	@JsonProperty("auto_approved")
	private Boolean autoApproved;
}

package in.codifi.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioRequestModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("customer_identifier")
	private String customerIdentifier;
	@JsonProperty("customer_name")
	private String customerName;
	@JsonProperty("reference_id")
	private String referenceId;
	@JsonProperty("actions")
	private List<DigioAction> actions;
	@JsonProperty("notify_customer")
	private Boolean notifyCustomer;
	@JsonProperty("generate_access_token")
	private Boolean generateAccessToken;
	@JsonProperty("transaction_id")
	private String transactionId;
}

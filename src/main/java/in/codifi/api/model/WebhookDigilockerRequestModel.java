package in.codifi.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookDigilockerRequestModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("id")
	private String id;
	@JsonProperty("customer_name")
	private String customerName;
	@JsonProperty("customer_identifier")
	private String customerIdentifier;
	@JsonProperty("kyc_request_id")
	private String kycRequestId;
	@JsonProperty("reference_id")
	private String referenceId;
	@JsonProperty("transaction_id")
	private String transactionId;
	@JsonProperty("state")
	private String state;
	@JsonProperty("shared_documents")
	private List<String> sharedDocuments;
}

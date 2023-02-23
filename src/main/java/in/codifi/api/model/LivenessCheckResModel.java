package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LivenessCheckResModel {
	@JsonProperty("doc_json")
	private DocJson docJson;
	@JsonProperty("doc_type")
	private String docType;
	@JsonProperty("error_message")
	private String errorMessage;
	@JsonProperty("req_id")
	private Integer reqId;
	@JsonProperty("success")
	private Boolean success;

	@JsonProperty("doc_json")
	public DocJson getDocJson() {
		return docJson;
	}

	@JsonProperty("doc_json")
	public void setDocJson(DocJson docJson) {
		this.docJson = docJson;
	}

	@JsonProperty("doc_type")
	public String getDocType() {
		return docType;
	}

	@JsonProperty("doc_type")
	public void setDocType(String docType) {
		this.docType = docType;
	}

	@JsonProperty("error_message")
	public String getErrorMessage() {
		return errorMessage;
	}

	@JsonProperty("error_message")
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@JsonProperty("req_id")
	public Integer getReqId() {
		return reqId;
	}

	@JsonProperty("req_id")
	public void setReqId(Integer reqId) {
		this.reqId = reqId;
	}

	@JsonProperty("success")
	public Boolean getSuccess() {
		return success;
	}

	@JsonProperty("success")
	public void setSuccess(Boolean success) {
		this.success = success;
	}
}

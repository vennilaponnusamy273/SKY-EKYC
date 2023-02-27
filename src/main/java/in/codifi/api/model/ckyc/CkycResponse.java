package in.codifi.api.model.ckyc;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "req_id", "success", "error_message", "ckyc_remarks", "result" })
public class CkycResponse {
	@JsonProperty("req_id")
	private String reqId;
	@JsonProperty("success")
	private Boolean success;
	@JsonProperty("error_message")
	private Object errorMessage;
	@JsonProperty("ckyc_remarks")
	private String ckycRemarks;
	@JsonProperty("result")
	private Result result;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("req_id")
	public String getReqId() {
		return reqId;
	}

	@JsonProperty("req_id")
	public void setReqId(String reqId) {
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

	@JsonProperty("error_message")
	public Object getErrorMessage() {
		return errorMessage;
	}

	@JsonProperty("error_message")
	public void setErrorMessage(Object errorMessage) {
		this.errorMessage = errorMessage;
	}

	@JsonProperty("ckyc_remarks")
	public String getCkycRemarks() {
		return ckycRemarks;
	}

	@JsonProperty("ckyc_remarks")
	public void setCkycRemarks(String ckycRemarks) {
		this.ckycRemarks = ckycRemarks;
	}

	@JsonProperty("result")
	public Result getResult() {
		return result;
	}

	@JsonProperty("result")
	public void setResult(Result result) {
		this.result = result;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}

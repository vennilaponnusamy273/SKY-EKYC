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
@JsonPropertyOrder({ "SEQUENCE_NO", "IDENT_TYPE", "IDENT_NUM", "IDVER_STATUS", "IDENT_NAME" })
public class Identity {
	@JsonProperty("SEQUENCE_NO")
	private String sequenceNo;
	@JsonProperty("IDENT_TYPE")
	private String identType;
	@JsonProperty("IDENT_NUM")
	private String identNum;
	@JsonProperty("IDVER_STATUS")
	private String idverStatus;
	@JsonProperty("IDENT_NAME")
	private String identName;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("SEQUENCE_NO")
	public String getSequenceNo() {
		return sequenceNo;
	}

	@JsonProperty("SEQUENCE_NO")
	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@JsonProperty("IDENT_TYPE")
	public String getIdentType() {
		return identType;
	}

	@JsonProperty("IDENT_TYPE")
	public void setIdentType(String identType) {
		this.identType = identType;
	}

	@JsonProperty("IDENT_NUM")
	public String getIdentNum() {
		return identNum;
	}

	@JsonProperty("IDENT_NUM")
	public void setIdentNum(String identNum) {
		this.identNum = identNum;
	}

	@JsonProperty("IDVER_STATUS")
	public String getIdverStatus() {
		return idverStatus;
	}

	@JsonProperty("IDVER_STATUS")
	public void setIdverStatus(String idverStatus) {
		this.idverStatus = idverStatus;
	}

	@JsonProperty("IDENT_NAME")
	public String getIdentName() {
		return identName;
	}

	@JsonProperty("IDENT_NAME")
	public void setIdentName(String identName) {
		this.identName = identName;
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

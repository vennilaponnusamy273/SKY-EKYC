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
@JsonPropertyOrder({ "SEQUENCE_NO", "IMAGE_TYPE", "IMAGE_CODE", "GLOBAL_FLAG", "BRANCH_CODE", "IMAGE_DATA",
		"IMAGE_NAME" })
public class Image {
	@JsonProperty("SEQUENCE_NO")
	private String sequenceNo;
	@JsonProperty("IMAGE_TYPE")
	private String imageType;
	@JsonProperty("IMAGE_CODE")
	private String imageCode;
	@JsonProperty("GLOBAL_FLAG")
	private String globalFlag;
	@JsonProperty("BRANCH_CODE")
	private String branchCode;
	@JsonProperty("IMAGE_DATA")
	private String imageData;
	@JsonProperty("IMAGE_NAME")
	private String imageName;
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

	@JsonProperty("IMAGE_TYPE")
	public String getImageType() {
		return imageType;
	}

	@JsonProperty("IMAGE_TYPE")
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	@JsonProperty("IMAGE_CODE")
	public String getImageCode() {
		return imageCode;
	}

	@JsonProperty("IMAGE_CODE")
	public void setImageCode(String imageCode) {
		this.imageCode = imageCode;
	}

	@JsonProperty("GLOBAL_FLAG")
	public String getGlobalFlag() {
		return globalFlag;
	}

	@JsonProperty("GLOBAL_FLAG")
	public void setGlobalFlag(String globalFlag) {
		this.globalFlag = globalFlag;
	}

	@JsonProperty("BRANCH_CODE")
	public String getBranchCode() {
		return branchCode;
	}

	@JsonProperty("BRANCH_CODE")
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@JsonProperty("IMAGE_DATA")
	public String getImageData() {
		return imageData;
	}

	@JsonProperty("IMAGE_DATA")
	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	@JsonProperty("IMAGE_NAME")
	public String getImageName() {
		return imageName;
	}

	@JsonProperty("IMAGE_NAME")
	public void setImageName(String imageName) {
		this.imageName = imageName;
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

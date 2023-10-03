package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostOfficeModel {
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Description")
	private Object description;
	@JsonProperty("BranchType")
	private String branchType;
	@JsonProperty("DeliveryStatus")
	private String deliveryStatus;
	@JsonProperty("Circle")
	private String circle;
	@JsonProperty("District")
	private String district;
	@JsonProperty("Division")
	private String division;
	@JsonProperty("Region")
	private String region;
	@JsonProperty("Block")
	private String block;
	@JsonProperty("State")
	private String state;
	@JsonProperty("Country")
	private String country;
	@JsonProperty("Pincode")
	private String pincode;

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("Description")
	public Object getDescription() {
		return description;
	}

	@JsonProperty("Description")
	public void setDescription(Object description) {
		this.description = description;
	}

	@JsonProperty("BranchType")
	public String getBranchType() {
		return branchType;
	}

	@JsonProperty("BranchType")
	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}

	@JsonProperty("DeliveryStatus")
	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	@JsonProperty("DeliveryStatus")
	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	@JsonProperty("Circle")
	public String getCircle() {
		return circle;
	}

	@JsonProperty("Circle")
	public void setCircle(String circle) {
		this.circle = circle;
	}

	@JsonProperty("District")
	public String getDistrict() {
		return district;
	}

	@JsonProperty("District")
	public void setDistrict(String district) {
		this.district = district;
	}

	@JsonProperty("Division")
	public String getDivision() {
		return division;
	}

	@JsonProperty("Division")
	public void setDivision(String division) {
		this.division = division;
	}

	@JsonProperty("Region")
	public String getRegion() {
		return region;
	}

	@JsonProperty("Region")
	public void setRegion(String region) {
		this.region = region;
	}

	@JsonProperty("Block")
	public String getBlock() {
		return block;
	}

	@JsonProperty("Block")
	public void setBlock(String block) {
		this.block = block;
	}

	@JsonProperty("State")
	public String getState() {
		return state;
	}

	@JsonProperty("State")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("Country")
	public String getCountry() {
		return country;
	}

	@JsonProperty("Country")
	public void setCountry(String country) {
		this.country = country;
	}

	@JsonProperty("Pincode")
	public String getPincode() {
		return pincode;
	}

	@JsonProperty("Pincode")
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
}

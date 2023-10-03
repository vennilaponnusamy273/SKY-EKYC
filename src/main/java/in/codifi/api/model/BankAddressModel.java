package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAddressModel {
	@JsonProperty("BRANCH")
	private String branch;
	@JsonProperty("NEFT")
	private Boolean neft;
	@JsonProperty("CITY")
	private String city;
	@JsonProperty("MICR")
	private String micr;
	@JsonProperty("UPI")
	private Boolean upi;
	@JsonProperty("STATE")
	private String state;
	@JsonProperty("ISO3166")
	private String iso3166;
	@JsonProperty("CONTACT")
	private String contact;
	@JsonProperty("RTGS")
	private Boolean rtgs;
	@JsonProperty("DISTRICT")
	private String district;
	@JsonProperty("SWIFT")
	private Object swift;
	@JsonProperty("ADDRESS")
	private String address;
	@JsonProperty("IMPS")
	private Boolean imps;
	@JsonProperty("CENTRE")
	private String centre;
	@JsonProperty("BANK")
	private String bank;
	@JsonProperty("BANKCODE")
	private String bankcode;
	@JsonProperty("IFSC")
	private String ifsc;

	@JsonProperty("BRANCH")
	public String getBranch() {
		return branch;
	}

	@JsonProperty("BRANCH")
	public void setBranch(String branch) {
		this.branch = branch;
	}

	@JsonProperty("NEFT")
	public Boolean getNeft() {
		return neft;
	}

	@JsonProperty("NEFT")
	public void setNeft(Boolean neft) {
		this.neft = neft;
	}

	@JsonProperty("CITY")
	public String getCity() {
		return city;
	}

	@JsonProperty("CITY")
	public void setCity(String city) {
		this.city = city;
	}

	@JsonProperty("MICR")
	public String getMicr() {
		return micr;
	}

	@JsonProperty("MICR")
	public void setMicr(String micr) {
		this.micr = micr;
	}

	@JsonProperty("UPI")
	public Boolean getUpi() {
		return upi;
	}

	@JsonProperty("UPI")
	public void setUpi(Boolean upi) {
		this.upi = upi;
	}

	@JsonProperty("STATE")
	public String getState() {
		return state;
	}

	@JsonProperty("STATE")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("ISO3166")
	public String getIso3166() {
		return iso3166;
	}

	@JsonProperty("ISO3166")
	public void setIso3166(String iso3166) {
		this.iso3166 = iso3166;
	}

	@JsonProperty("CONTACT")
	public String getContact() {
		return contact;
	}

	@JsonProperty("CONTACT")
	public void setContact(String contact) {
		this.contact = contact;
	}

	@JsonProperty("RTGS")
	public Boolean getRtgs() {
		return rtgs;
	}

	@JsonProperty("RTGS")
	public void setRtgs(Boolean rtgs) {
		this.rtgs = rtgs;
	}

	@JsonProperty("DISTRICT")
	public String getDistrict() {
		return district;
	}

	@JsonProperty("DISTRICT")
	public void setDistrict(String district) {
		this.district = district;
	}

	@JsonProperty("SWIFT")
	public Object getSwift() {
		return swift;
	}

	@JsonProperty("SWIFT")
	public void setSwift(Object swift) {
		this.swift = swift;
	}

	@JsonProperty("ADDRESS")
	public String getAddress() {
		return address;
	}

	@JsonProperty("ADDRESS")
	public void setAddress(String address) {
		this.address = address;
	}

	@JsonProperty("IMPS")
	public Boolean getImps() {
		return imps;
	}

	@JsonProperty("IMPS")
	public void setImps(Boolean imps) {
		this.imps = imps;
	}

	@JsonProperty("CENTRE")
	public String getCentre() {
		return centre;
	}

	@JsonProperty("CENTRE")
	public void setCentre(String centre) {
		this.centre = centre;
	}

	@JsonProperty("BANK")
	public String getBank() {
		return bank;
	}

	@JsonProperty("BANK")
	public void setBank(String bank) {
		this.bank = bank;
	}

	@JsonProperty("BANKCODE")
	public String getBankcode() {
		return bankcode;
	}

	@JsonProperty("BANKCODE")
	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	@JsonProperty("IFSC")
	public String getIfsc() {
		return ifsc;
	}

	@JsonProperty("IFSC")
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
}

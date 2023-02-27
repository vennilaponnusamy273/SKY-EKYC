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
@JsonPropertyOrder({ "CONSTI_TYPE", "ACC_TYPE", "CKYC_NO", "PREFIX", "FNAME", "MNAME", "LNAME", "FULLNAME",
		"MAIDEN_PREFIX", "MAIDEN_FNAME", "MAIDEN_MNAME", "MAIDEN_LNAME", "MAIDEN_FULLNAME", "FATHERSPOUSE_FLAG",
		"FATHER_PREFIX", "FATHER_FNAME", "FATHER_MNAME", "FATHER_LNAME", "FATHER_FULLNAME", "MOTHER_PREFIX",
		"MOTHER_FNAME", "MOTHER_MNAME", "MOTHER_LNAME", "MOTHER_FULLNAME", "GENDER", "DOB", "PAN", "PERM_LINE1",
		"PERM_LINE2", "PERM_LINE3", "PERM_CITY", "PERM_DIST", "PERM_STATE", "PERM_COUNTRY", "PERM_PIN", "PERM_POA",
		"PERM_CORRES_SAMEFLAG", "CORRES_LINE1", "CORRES_LINE2", "CORRES_LINE3", "CORRES_CITY", "CORRES_DIST",
		"CORRES_STATE", "CORRES_COUNTRY", "CORRES_PIN", "CORRES_POA", "RESI_STD_CODE", "RESI_TEL_NUM", "OFF_STD_CODE",
		"OFF_TEL_NUM", "MOB_CODE", "MOB_NUM", "EMAIL", "REMARKS", "DEC_DATE", "DEC_PLACE", "KYC_DATE", "DOC_SUB",
		"KYC_NAME", "KYC_DESIGNATION", "KYC_BRANCH", "KYC_EMPCODE", "ORG_NAME", "ORG_CODE", "NUM_IDENTITY",
		"NUM_RELATED", "NUM_IMAGES", "UPDATED_DATE" })
public class PersonalDetails {
	@JsonProperty("CONSTI_TYPE")
	private String constiType;
	@JsonProperty("ACC_TYPE")
	private String accType;
	@JsonProperty("CKYC_NO")
	private String ckycNo;
	@JsonProperty("PREFIX")
	private String prefix;
	@JsonProperty("FNAME")
	private String fname;
	@JsonProperty("MNAME")
	private String mname;
	@JsonProperty("LNAME")
	private String lname;
	@JsonProperty("FULLNAME")
	private String fullname;
	@JsonProperty("MAIDEN_PREFIX")
	private Object maidenPrefix;
	@JsonProperty("MAIDEN_FNAME")
	private Object maidenFname;
	@JsonProperty("MAIDEN_MNAME")
	private Object maidenMname;
	@JsonProperty("MAIDEN_LNAME")
	private Object maidenLname;
	@JsonProperty("MAIDEN_FULLNAME")
	private Object maidenFullname;
	@JsonProperty("FATHERSPOUSE_FLAG")
	private Object fatherspouseFlag;
	@JsonProperty("FATHER_PREFIX")
	private String fatherPrefix;
	@JsonProperty("FATHER_FNAME")
	private String fatherFname;
	@JsonProperty("FATHER_MNAME")
	private Object fatherMname;
	@JsonProperty("FATHER_LNAME")
	private Object fatherLname;
	@JsonProperty("FATHER_FULLNAME")
	private String fatherFullname;
	@JsonProperty("MOTHER_PREFIX")
	private String motherPrefix;
	@JsonProperty("MOTHER_FNAME")
	private String motherFname;
	@JsonProperty("MOTHER_MNAME")
	private Object motherMname;
	@JsonProperty("MOTHER_LNAME")
	private Object motherLname;
	@JsonProperty("MOTHER_FULLNAME")
	private String motherFullname;
	@JsonProperty("GENDER")
	private String gender;
	@JsonProperty("DOB")
	private String dob;
	@JsonProperty("PAN")
	private String pan;
	@JsonProperty("PERM_LINE1")
	private String permLine1;
	@JsonProperty("PERM_LINE2")
	private String permLine2;
	@JsonProperty("PERM_LINE3")
	private Object permLine3;
	@JsonProperty("PERM_CITY")
	private String permCity;
	@JsonProperty("PERM_DIST")
	private String permDist;
	@JsonProperty("PERM_STATE")
	private String permState;
	@JsonProperty("PERM_COUNTRY")
	private String permCountry;
	@JsonProperty("PERM_PIN")
	private String permPin;
	@JsonProperty("PERM_POA")
	private String permPoa;
	@JsonProperty("PERM_CORRES_SAMEFLAG")
	private String permCorresSameflag;
	@JsonProperty("CORRES_LINE1")
	private String corresLine1;
	@JsonProperty("CORRES_LINE2")
	private String corresLine2;
	@JsonProperty("CORRES_LINE3")
	private String corresLine3;
	@JsonProperty("CORRES_CITY")
	private String corresCity;
	@JsonProperty("CORRES_DIST")
	private String corresDist;
	@JsonProperty("CORRES_STATE")
	private String corresState;
	@JsonProperty("CORRES_COUNTRY")
	private String corresCountry;
	@JsonProperty("CORRES_PIN")
	private String corresPin;
	@JsonProperty("CORRES_POA")
	private String corresPoa;
	@JsonProperty("RESI_STD_CODE")
	private Object resiStdCode;
	@JsonProperty("RESI_TEL_NUM")
	private Object resiTelNum;
	@JsonProperty("OFF_STD_CODE")
	private Object offStdCode;
	@JsonProperty("OFF_TEL_NUM")
	private Object offTelNum;
	@JsonProperty("MOB_CODE")
	private String mobCode;
	@JsonProperty("MOB_NUM")
	private String mobNum;
	@JsonProperty("EMAIL")
	private String email;
	@JsonProperty("REMARKS")
	private Object remarks;
	@JsonProperty("DEC_DATE")
	private String decDate;
	@JsonProperty("DEC_PLACE")
	private String decPlace;
	@JsonProperty("KYC_DATE")
	private String kycDate;
	@JsonProperty("DOC_SUB")
	private String docSub;
	@JsonProperty("KYC_NAME")
	private String kycName;
	@JsonProperty("KYC_DESIGNATION")
	private String kycDesignation;
	@JsonProperty("KYC_BRANCH")
	private String kycBranch;
	@JsonProperty("KYC_EMPCODE")
	private String kycEmpcode;
	@JsonProperty("ORG_NAME")
	private String orgName;
	@JsonProperty("ORG_CODE")
	private String orgCode;
	@JsonProperty("NUM_IDENTITY")
	private String numIdentity;
	@JsonProperty("NUM_RELATED")
	private String numRelated;
	@JsonProperty("NUM_IMAGES")
	private String numImages;
	@JsonProperty("UPDATED_DATE")
	private String updatedDate;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("CONSTI_TYPE")
	public String getConstiType() {
		return constiType;
	}

	@JsonProperty("CONSTI_TYPE")
	public void setConstiType(String constiType) {
		this.constiType = constiType;
	}

	@JsonProperty("ACC_TYPE")
	public String getAccType() {
		return accType;
	}

	@JsonProperty("ACC_TYPE")
	public void setAccType(String accType) {
		this.accType = accType;
	}

	@JsonProperty("CKYC_NO")
	public String getCkycNo() {
		return ckycNo;
	}

	@JsonProperty("CKYC_NO")
	public void setCkycNo(String ckycNo) {
		this.ckycNo = ckycNo;
	}

	@JsonProperty("PREFIX")
	public String getPrefix() {
		return prefix;
	}

	@JsonProperty("PREFIX")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@JsonProperty("FNAME")
	public String getFname() {
		return fname;
	}

	@JsonProperty("FNAME")
	public void setFname(String fname) {
		this.fname = fname;
	}

	@JsonProperty("MNAME")
	public String getMname() {
		return mname;
	}

	@JsonProperty("MNAME")
	public void setMname(String mname) {
		this.mname = mname;
	}

	@JsonProperty("LNAME")
	public String getLname() {
		return lname;
	}

	@JsonProperty("LNAME")
	public void setLname(String lname) {
		this.lname = lname;
	}

	@JsonProperty("FULLNAME")
	public String getFullname() {
		return fullname;
	}

	@JsonProperty("FULLNAME")
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@JsonProperty("MAIDEN_PREFIX")
	public Object getMaidenPrefix() {
		return maidenPrefix;
	}

	@JsonProperty("MAIDEN_PREFIX")
	public void setMaidenPrefix(Object maidenPrefix) {
		this.maidenPrefix = maidenPrefix;
	}

	@JsonProperty("MAIDEN_FNAME")
	public Object getMaidenFname() {
		return maidenFname;
	}

	@JsonProperty("MAIDEN_FNAME")
	public void setMaidenFname(Object maidenFname) {
		this.maidenFname = maidenFname;
	}

	@JsonProperty("MAIDEN_MNAME")
	public Object getMaidenMname() {
		return maidenMname;
	}

	@JsonProperty("MAIDEN_MNAME")
	public void setMaidenMname(Object maidenMname) {
		this.maidenMname = maidenMname;
	}

	@JsonProperty("MAIDEN_LNAME")
	public Object getMaidenLname() {
		return maidenLname;
	}

	@JsonProperty("MAIDEN_LNAME")
	public void setMaidenLname(Object maidenLname) {
		this.maidenLname = maidenLname;
	}

	@JsonProperty("MAIDEN_FULLNAME")
	public Object getMaidenFullname() {
		return maidenFullname;
	}

	@JsonProperty("MAIDEN_FULLNAME")
	public void setMaidenFullname(Object maidenFullname) {
		this.maidenFullname = maidenFullname;
	}

	@JsonProperty("FATHERSPOUSE_FLAG")
	public Object getFatherspouseFlag() {
		return fatherspouseFlag;
	}

	@JsonProperty("FATHERSPOUSE_FLAG")
	public void setFatherspouseFlag(Object fatherspouseFlag) {
		this.fatherspouseFlag = fatherspouseFlag;
	}

	@JsonProperty("FATHER_PREFIX")
	public String getFatherPrefix() {
		return fatherPrefix;
	}

	@JsonProperty("FATHER_PREFIX")
	public void setFatherPrefix(String fatherPrefix) {
		this.fatherPrefix = fatherPrefix;
	}

	@JsonProperty("FATHER_FNAME")
	public String getFatherFname() {
		return fatherFname;
	}

	@JsonProperty("FATHER_FNAME")
	public void setFatherFname(String fatherFname) {
		this.fatherFname = fatherFname;
	}

	@JsonProperty("FATHER_MNAME")
	public Object getFatherMname() {
		return fatherMname;
	}

	@JsonProperty("FATHER_MNAME")
	public void setFatherMname(Object fatherMname) {
		this.fatherMname = fatherMname;
	}

	@JsonProperty("FATHER_LNAME")
	public Object getFatherLname() {
		return fatherLname;
	}

	@JsonProperty("FATHER_LNAME")
	public void setFatherLname(Object fatherLname) {
		this.fatherLname = fatherLname;
	}

	@JsonProperty("FATHER_FULLNAME")
	public String getFatherFullname() {
		return fatherFullname;
	}

	@JsonProperty("FATHER_FULLNAME")
	public void setFatherFullname(String fatherFullname) {
		this.fatherFullname = fatherFullname;
	}

	@JsonProperty("MOTHER_PREFIX")
	public String getMotherPrefix() {
		return motherPrefix;
	}

	@JsonProperty("MOTHER_PREFIX")
	public void setMotherPrefix(String motherPrefix) {
		this.motherPrefix = motherPrefix;
	}

	@JsonProperty("MOTHER_FNAME")
	public String getMotherFname() {
		return motherFname;
	}

	@JsonProperty("MOTHER_FNAME")
	public void setMotherFname(String motherFname) {
		this.motherFname = motherFname;
	}

	@JsonProperty("MOTHER_MNAME")
	public Object getMotherMname() {
		return motherMname;
	}

	@JsonProperty("MOTHER_MNAME")
	public void setMotherMname(Object motherMname) {
		this.motherMname = motherMname;
	}

	@JsonProperty("MOTHER_LNAME")
	public Object getMotherLname() {
		return motherLname;
	}

	@JsonProperty("MOTHER_LNAME")
	public void setMotherLname(Object motherLname) {
		this.motherLname = motherLname;
	}

	@JsonProperty("MOTHER_FULLNAME")
	public String getMotherFullname() {
		return motherFullname;
	}

	@JsonProperty("MOTHER_FULLNAME")
	public void setMotherFullname(String motherFullname) {
		this.motherFullname = motherFullname;
	}

	@JsonProperty("GENDER")
	public String getGender() {
		return gender;
	}

	@JsonProperty("GENDER")
	public void setGender(String gender) {
		this.gender = gender;
	}

	@JsonProperty("DOB")
	public String getDob() {
		return dob;
	}

	@JsonProperty("DOB")
	public void setDob(String dob) {
		this.dob = dob;
	}

	@JsonProperty("PAN")
	public String getPan() {
		return pan;
	}

	@JsonProperty("PAN")
	public void setPan(String pan) {
		this.pan = pan;
	}

	@JsonProperty("PERM_LINE1")
	public String getPermLine1() {
		return permLine1;
	}

	@JsonProperty("PERM_LINE1")
	public void setPermLine1(String permLine1) {
		this.permLine1 = permLine1;
	}

	@JsonProperty("PERM_LINE2")
	public String getPermLine2() {
		return permLine2;
	}

	@JsonProperty("PERM_LINE2")
	public void setPermLine2(String permLine2) {
		this.permLine2 = permLine2;
	}

	@JsonProperty("PERM_LINE3")
	public Object getPermLine3() {
		return permLine3;
	}

	@JsonProperty("PERM_LINE3")
	public void setPermLine3(Object permLine3) {
		this.permLine3 = permLine3;
	}

	@JsonProperty("PERM_CITY")
	public String getPermCity() {
		return permCity;
	}

	@JsonProperty("PERM_CITY")
	public void setPermCity(String permCity) {
		this.permCity = permCity;
	}

	@JsonProperty("PERM_DIST")
	public String getPermDist() {
		return permDist;
	}

	@JsonProperty("PERM_DIST")
	public void setPermDist(String permDist) {
		this.permDist = permDist;
	}

	@JsonProperty("PERM_STATE")
	public String getPermState() {
		return permState;
	}

	@JsonProperty("PERM_STATE")
	public void setPermState(String permState) {
		this.permState = permState;
	}

	@JsonProperty("PERM_COUNTRY")
	public String getPermCountry() {
		return permCountry;
	}

	@JsonProperty("PERM_COUNTRY")
	public void setPermCountry(String permCountry) {
		this.permCountry = permCountry;
	}

	@JsonProperty("PERM_PIN")
	public String getPermPin() {
		return permPin;
	}

	@JsonProperty("PERM_PIN")
	public void setPermPin(String permPin) {
		this.permPin = permPin;
	}

	@JsonProperty("PERM_POA")
	public String getPermPoa() {
		return permPoa;
	}

	@JsonProperty("PERM_POA")
	public void setPermPoa(String permPoa) {
		this.permPoa = permPoa;
	}

	@JsonProperty("PERM_CORRES_SAMEFLAG")
	public String getPermCorresSameflag() {
		return permCorresSameflag;
	}

	@JsonProperty("PERM_CORRES_SAMEFLAG")
	public void setPermCorresSameflag(String permCorresSameflag) {
		this.permCorresSameflag = permCorresSameflag;
	}

	@JsonProperty("CORRES_LINE1")
	public String getCorresLine1() {
		return corresLine1;
	}

	@JsonProperty("CORRES_LINE1")
	public void setCorresLine1(String corresLine1) {
		this.corresLine1 = corresLine1;
	}

	@JsonProperty("CORRES_LINE2")
	public String getCorresLine2() {
		return corresLine2;
	}

	@JsonProperty("CORRES_LINE2")
	public void setCorresLine2(String corresLine2) {
		this.corresLine2 = corresLine2;
	}

	@JsonProperty("CORRES_LINE3")
	public String getCorresLine3() {
		return corresLine3;
	}

	@JsonProperty("CORRES_LINE3")
	public void setCorresLine3(String corresLine3) {
		this.corresLine3 = corresLine3;
	}

	@JsonProperty("CORRES_CITY")
	public String getCorresCity() {
		return corresCity;
	}

	@JsonProperty("CORRES_CITY")
	public void setCorresCity(String corresCity) {
		this.corresCity = corresCity;
	}

	@JsonProperty("CORRES_DIST")
	public String getCorresDist() {
		return corresDist;
	}

	@JsonProperty("CORRES_DIST")
	public void setCorresDist(String corresDist) {
		this.corresDist = corresDist;
	}

	@JsonProperty("CORRES_STATE")
	public String getCorresState() {
		return corresState;
	}

	@JsonProperty("CORRES_STATE")
	public void setCorresState(String corresState) {
		this.corresState = corresState;
	}

	@JsonProperty("CORRES_COUNTRY")
	public String getCorresCountry() {
		return corresCountry;
	}

	@JsonProperty("CORRES_COUNTRY")
	public void setCorresCountry(String corresCountry) {
		this.corresCountry = corresCountry;
	}

	@JsonProperty("CORRES_PIN")
	public String getCorresPin() {
		return corresPin;
	}

	@JsonProperty("CORRES_PIN")
	public void setCorresPin(String corresPin) {
		this.corresPin = corresPin;
	}

	@JsonProperty("CORRES_POA")
	public String getCorresPoa() {
		return corresPoa;
	}

	@JsonProperty("CORRES_POA")
	public void setCorresPoa(String corresPoa) {
		this.corresPoa = corresPoa;
	}

	@JsonProperty("RESI_STD_CODE")
	public Object getResiStdCode() {
		return resiStdCode;
	}

	@JsonProperty("RESI_STD_CODE")
	public void setResiStdCode(Object resiStdCode) {
		this.resiStdCode = resiStdCode;
	}

	@JsonProperty("RESI_TEL_NUM")
	public Object getResiTelNum() {
		return resiTelNum;
	}

	@JsonProperty("RESI_TEL_NUM")
	public void setResiTelNum(Object resiTelNum) {
		this.resiTelNum = resiTelNum;
	}

	@JsonProperty("OFF_STD_CODE")
	public Object getOffStdCode() {
		return offStdCode;
	}

	@JsonProperty("OFF_STD_CODE")
	public void setOffStdCode(Object offStdCode) {
		this.offStdCode = offStdCode;
	}

	@JsonProperty("OFF_TEL_NUM")
	public Object getOffTelNum() {
		return offTelNum;
	}

	@JsonProperty("OFF_TEL_NUM")
	public void setOffTelNum(Object offTelNum) {
		this.offTelNum = offTelNum;
	}

	@JsonProperty("MOB_CODE")
	public String getMobCode() {
		return mobCode;
	}

	@JsonProperty("MOB_CODE")
	public void setMobCode(String mobCode) {
		this.mobCode = mobCode;
	}

	@JsonProperty("MOB_NUM")
	public String getMobNum() {
		return mobNum;
	}

	@JsonProperty("MOB_NUM")
	public void setMobNum(String mobNum) {
		this.mobNum = mobNum;
	}

	@JsonProperty("EMAIL")
	public String getEmail() {
		return email;
	}

	@JsonProperty("EMAIL")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("REMARKS")
	public Object getRemarks() {
		return remarks;
	}

	@JsonProperty("REMARKS")
	public void setRemarks(Object remarks) {
		this.remarks = remarks;
	}

	@JsonProperty("DEC_DATE")
	public String getDecDate() {
		return decDate;
	}

	@JsonProperty("DEC_DATE")
	public void setDecDate(String decDate) {
		this.decDate = decDate;
	}

	@JsonProperty("DEC_PLACE")
	public String getDecPlace() {
		return decPlace;
	}

	@JsonProperty("DEC_PLACE")
	public void setDecPlace(String decPlace) {
		this.decPlace = decPlace;
	}

	@JsonProperty("KYC_DATE")
	public String getKycDate() {
		return kycDate;
	}

	@JsonProperty("KYC_DATE")
	public void setKycDate(String kycDate) {
		this.kycDate = kycDate;
	}

	@JsonProperty("DOC_SUB")
	public String getDocSub() {
		return docSub;
	}

	@JsonProperty("DOC_SUB")
	public void setDocSub(String docSub) {
		this.docSub = docSub;
	}

	@JsonProperty("KYC_NAME")
	public String getKycName() {
		return kycName;
	}

	@JsonProperty("KYC_NAME")
	public void setKycName(String kycName) {
		this.kycName = kycName;
	}

	@JsonProperty("KYC_DESIGNATION")
	public String getKycDesignation() {
		return kycDesignation;
	}

	@JsonProperty("KYC_DESIGNATION")
	public void setKycDesignation(String kycDesignation) {
		this.kycDesignation = kycDesignation;
	}

	@JsonProperty("KYC_BRANCH")
	public String getKycBranch() {
		return kycBranch;
	}

	@JsonProperty("KYC_BRANCH")
	public void setKycBranch(String kycBranch) {
		this.kycBranch = kycBranch;
	}

	@JsonProperty("KYC_EMPCODE")
	public String getKycEmpcode() {
		return kycEmpcode;
	}

	@JsonProperty("KYC_EMPCODE")
	public void setKycEmpcode(String kycEmpcode) {
		this.kycEmpcode = kycEmpcode;
	}

	@JsonProperty("ORG_NAME")
	public String getOrgName() {
		return orgName;
	}

	@JsonProperty("ORG_NAME")
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@JsonProperty("ORG_CODE")
	public String getOrgCode() {
		return orgCode;
	}

	@JsonProperty("ORG_CODE")
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	@JsonProperty("NUM_IDENTITY")
	public String getNumIdentity() {
		return numIdentity;
	}

	@JsonProperty("NUM_IDENTITY")
	public void setNumIdentity(String numIdentity) {
		this.numIdentity = numIdentity;
	}

	@JsonProperty("NUM_RELATED")
	public String getNumRelated() {
		return numRelated;
	}

	@JsonProperty("NUM_RELATED")
	public void setNumRelated(String numRelated) {
		this.numRelated = numRelated;
	}

	@JsonProperty("NUM_IMAGES")
	public String getNumImages() {
		return numImages;
	}

	@JsonProperty("NUM_IMAGES")
	public void setNumImages(String numImages) {
		this.numImages = numImages;
	}

	@JsonProperty("UPDATED_DATE")
	public String getUpdatedDate() {
		return updatedDate;
	}

	@JsonProperty("UPDATED_DATE")
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
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

package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="tbl_ckyc_Response")
public class ResponseCkyc  extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "application_id")
	private Long applicationId;
	
	@Column(name="constiType")
	private String constiType;
	
	@Column(name="accType")
	private String accType;
	
	@Column(name="ckycNo")
	private String ckycNo;
	
	@Column(name="prefix")
	private String prefix;
	
	@Column(name="fname")
	private String fname;
	
	
	@Column(name="mname")
	private String mname;
	
	@Column(name="lname")
	private String lname;
	
	@Column(name="fullname")
	private String fullname;
	
	@Column(name="fatherPrefix")
	private String fatherPrefix;
	
	@Column(name="fatherFname")
	private String fatherFname;
	
	@Column(name="fatherMname")
	private String fatherMname;
	
	@Column(name="fatherLname")
	private String fatherLname;
	
	@Column(name="fatherFullname")
	private String fatherFullname;
	
	@Column(name="motherPrefix")
	private String motherPrefix;
	
	@Column(name="motherFname")
	private String motherFname;
	
	@Column(name="motherMname")
	private String motherMname;
	
	@Column(name="motherLname")
	private String motherLname;
	
	@Column(name="motherFullname")
	private String motherFullname;
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="dob")
	private String dob;
	
	@Column(name="pan")
	private String pan;
	
	@Column(name="permLine1")
	private String permLine1;
	
	@Column(name="permLine2")
	private String permLine2;
	
	@Column(name="permLine3")
	private String permLine3;
	
	@Column(name="permCity")
	private String permCity;
	
	@Column(name="permDist")
	private String permDist;
	
	@Column(name="permState")
	private String permState;
	
	@Column(name="permCountry")
	private String permCountry;
	
	@Column(name="permPin")
	private String permPin;
	@Column(name="permPoa")
	private String permPoa;
	
	@Column(name="permCorresSameflag")
	private String permCorresSameflag;
	
	@Column(name="corresLine1")
	private String corresLine1;
	
	@Column(name="corresLine2")
	private String corresLine2;
	
	@Column(name="corresLine3")
	private String corresLine3;
	
	@Column(name="corresCity")
	private String corresCity;
	
	@Column(name="corresDist")
	private String corresDist;
	
	@Column(name="corresState")
	private String corresState;
	
	@Column(name="corresCountry")
	private String corresCountry;
	
	@Column(name="corresPin")
	private String corresPin;
	
	@Column(name="corresPoa")
	private String corresPoa;
	
	@Column(name="mobCode")
	private String mobCode;
	
	@Column(name="mobNum")
	private String mobNum;
	
	@Column(name="email")
	private String email;
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="decDate")
	private String decDate;
	
	@Column(name="decPlace")
	private String decPlace;
	
	@Column(name="kycDate")
	private String kycDate;
	
	@Column(name="docSub")
	private String docSub;
	
	@Column(name="orgName")
	private String orgName;
	
	@Column(name="orgCode")
	private String orgCode;
	
	@Column(name="numIdentity")
	private String numIdentity;
	
	@Column(name="numRelated")
	private String numRelated;
	
	@Column(name="numImages")
	private String numImages;
	
	@Column(name="updatedDate")
	private String updatedDate;


}

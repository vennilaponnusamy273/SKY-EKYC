package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_referral_details")
@Getter
@Setter
public class ReferralEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "mobile_number")
	private Long mobileNo;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "is_Notify")
	private int isNotify = 0;

	@Column(name = "pan_number")
	private String panNumber;

	@Column(name = "url")
	private String url;

	@Column(name = "referral_by")
	private String referralBy;

	@Column(name = "ref_by_branch")
	private String refByBranch;

	@Column(name = "ref_by_name")
	private String refByName;

	@Column(name = "ref_by_designation")
	private String refByDesignation;

}

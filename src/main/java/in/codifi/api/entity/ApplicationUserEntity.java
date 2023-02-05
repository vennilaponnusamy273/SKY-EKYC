package in.codifi.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_APPLICATION_MASTER")
public class ApplicationUserEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "mobile_number")
	private Long mobileNo;

	@Column(name = "email_id")
	private String emailId;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "email_otp")
	private int emailOtp;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "sms_otp")
	private int smsOtp;

	@Column(name = "email_verified")
	private int emailVerified;

	@Column(name = "sms_verified")
	private int smsVerified;

	@Column(name = "pan_number")
	private String panNumber;

	@Column(name = "dob")
	private String dob;

	@Column(name = "stage")
	private int stage;

	@Column(name = "status")
	private String status;

}
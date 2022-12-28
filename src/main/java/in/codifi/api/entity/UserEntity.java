package in.codifi.api.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_USER_DETAILS")
public class UserEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "MOBILE_NUMBER")
	private Long mobileNo;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "EMAIL_OTP")
	private int emailOtp;

	@Column(name = "EMAIL_OTP_TIMESTAMP")
	private Long emailOtpTimeStamp;

	@Column(name = "SMS_OTP")
	private int smsOtp;

	@Column(name = "SMS_OTP_TIMESTAMP")
	private Long smsOtpTimeStamp;

	@Column(name = "EMAIL_VERIFIED")
	private int emailVerified;

	@Column(name = "SMS_VERIFIED")
	private int smsVerified;

	@Column(name = "PAN_NUMBER")
	private String panNumber;

	@Column(name = "DOB")
	private Date dateOfBirth;

	@Column(name = "CREATED_BY")
	private int createdBy;

	@Column(name = "CREATED_ON")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "UPDATED_BY")
	private int updatedBy;

	@Column(name = "UPDATED_ON")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "ACTIVE_STATUS")
	private int activeStatus;

}
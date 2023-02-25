package in.codifi.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_profile_details")
public class ProfileEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "father_name")
	private String fatherName;

	@Column(name = "applicant_name")
	private String applicantName;

	@Column(name = "mother_name")
	private String motherName;

	@Column(name = "occupation")
	private String occupation;

	@Column(name = "gender")
	private String gender;

	@Column(name = "title")
	private String title;

	@Column(name = "annual_income")
	private String annualIncome;

	@Column(name = "marital_status")
	private String maritalStatus;

	@Column(name = "political_exposure")
	private String politicalExposure;

	@Column(name = "trading_experience")
	private String tradingExperience;

	@Column(name = "legal_action")
	private String legalAction;

}

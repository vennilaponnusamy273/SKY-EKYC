package in.codifi.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_penny_drop")
public class PennyDropEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "confirm_penny")
	private int confirmPenny;

	@Column(name = "email")
	private String email;

	@Column(name = "mobileNumber")
	private String mobileNumber;

	@Column(name = "pan")
	private String pan;

	@Column(name = "accountNumber")
	private String accNumber;

	@Column(name = "ifsc")
	private String ifsc;

	@Column(name = "is_verified")
	private int isVerified;

	@Column(name = "penny_amount")
	private int pennyAmount;

	@Column(name = "contact_id")
	private String rzContactId;

	@Lob
	@Column(name = "req_contact_json")
	private String rzReqContactJson;

	@Lob
	@Column(name = "res_contact_json")
	private String rzResContactJson;

	@Column(name = "fund_account_id")
	private String rzFundAccountId;

	@Lob
	@Column(name = "req_fund_json")
	private String rzReqFundJson;

	@Lob
	@Column(name = "res_fund_json")
	private String rzResFundJson;

	@Column(name = "payout_id")
	private String rzPayoutId;

	@Lob
	@Column(name = "req_payout_json")
	private String rzReqPayoutJson;

	@Lob
	@Column(name = "res_payout_json")
	private String rzResPayoutJson;

	@Column(name = "holder_name")
	private String accountHolderName;

	@Lob
	@Column(name = "req_validation_json")
	private String rzReqValidationJson;

	@Lob
	@Column(name = "res_validation_json")
	private String rzResValidationJson;

}

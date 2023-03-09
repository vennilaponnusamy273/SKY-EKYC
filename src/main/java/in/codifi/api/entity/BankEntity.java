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
@Entity(name = "tbl_bank_details")
public class BankEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "micr")
	private String micr;

	@Column(name = "address")
	private String address;

	@Column(name = "branch_name")
	private String branchName;

	@Column(name = "pincode")
	private String pincode;

	@Column(name = "ifsc")
	private String ifsc;

	@Column(name = "account_number")
	private String accountNo;

	@Column(name = "verify_acc_numr")
	private String verifyAccNumber;

}

package in.codifi.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_penny_digio_details")
@Getter
@Setter
public class PennyVerificationResponseEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "application_id", nullable = false)
	private Long applicationId;
	
	@Column(name = "paymentid")
    private String paymentid;
	
	@Column(name = "verified")
    private String verified;


	@Column(name = "verified_at")
    private String verifiedAt;

	@Column(name = "beneficiary_name_with_bank")
    private String beneficiaryNameWithBank;

//	@Column(name = "fuzzy_match_result")
//    private String fuzzyMatchResult;
//
//	@Column(name = "fuzzy_match_score")
//    private int fuzzyMatchScore;
	
	@Column(name = "pennyConfirm")
    private int pennyConfirm=0;
	
	@Column(name = "account_number")
	private String accountNo;
    
}

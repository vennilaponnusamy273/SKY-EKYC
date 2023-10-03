package in.codifi.api.entity;

import java.io.Serializable;

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
@Entity(name = "tbl_erp_update_master")
public class UpdateErpEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "UserId")
	private String  userId;
	
	@Column(name = "email_id")
	private String emailId;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "pwd")
	private String password;
	
	@Column(name = "mobile_number")
	private Long mobileNo;
	
	@Column(name = "erpResponse")
	private String erpResponse;
	
	@Column(name = "docType")
	private String doctype;
	
	@Column(name = "erpapiType")
	private String erpApiType;
}

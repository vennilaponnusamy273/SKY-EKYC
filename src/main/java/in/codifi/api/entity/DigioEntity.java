package in.codifi.api.entity;

import java.io.Serializable;

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
@Entity(name = "tbl_digio_details")
public class DigioEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "request_url")
	private String requestUrl;

	@Column(name = "req_id")
	private String requestId;
	
	@Column(name = "execution_request_id")
	private String xmlrequestId;

	@Column(name = "random_key")
	private String randomKey;

	@Lob
	@Column(name = "ini_res_json")
	private String iniResjson;

	@Lob
	@Column(name = "wh_res_json")
	private String whResjson;

}

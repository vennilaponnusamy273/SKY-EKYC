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
@Entity(name = "tbl_cams_details")
public class CamsEntity extends CommonEntity implements Serializable {

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

	@Column(name = "session_id")
	private String sessionid;

	@Column(name = "clienttxt_id")
	private String clientTxtId;

	@Column(name = "token")
	private String token;

	@Column(name = "consentHandleid")
	private String consentHandleId;

	@Column(name = "redirectUrl")
	private String redirectUrl;
}

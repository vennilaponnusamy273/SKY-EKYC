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

@Entity(name = "tbl_ivr_details")
@Getter
@Setter
public class IvrEntity extends CommonEntity implements Serializable {

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

	@Column(name = "url")
	private String url;

	@Column(name = "attachement")
	private String attachement;

	@Column(name = "attachement_url")
	private String attachementUrl;

	@Column(name = "type_of_proof")
	private String typeOfProof;

	@Column(name = "document_type")
	private String documentType;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "latitude")
	private String latitude;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "longitude")
	private String longitude;

}

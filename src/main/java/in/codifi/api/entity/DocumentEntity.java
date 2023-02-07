package in.codifi.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_document_details")
@Getter
@Setter
public class DocumentEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "attachement")
	private String attachement;

	@Column(name = "attachement_url")
	private String attachementUrl;

	@Column(name = "type_of_proof")
	private String typeOfProof;

	@Column(name = "password")
	private String password;

}

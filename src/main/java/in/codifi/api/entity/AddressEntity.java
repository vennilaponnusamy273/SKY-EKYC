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
@Entity(name = "TBL_ADDRESS_DETAILS")
public class AddressEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "access_token")
	private String accessToken;

	@Column(name = "care_of")
	private String co;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "flatno")
	private String flatNo;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "landmark")
	private String landmark;

	@Column(name = "street")
	private String street;

	@Column(name = "district")
	private String district;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "pincode")
	private Long pincode;
}

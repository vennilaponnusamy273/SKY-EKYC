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

	@Column(name = "is_digi")
	private int isdigi;

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

	@Column(name = "is_kra")
	private int isKra;

	@Column(name = "kra_address_1")
	private String kraAddress1;

	@Column(name = "kra_address_2")
	private String kraAddress2;

	@Column(name = "kra_address_3")
	private String kraAddress3;

	@Column(name = "kra_pin")
	private int kraPin;

	@Column(name = "kra_city")
	private String kraCity;

	@Column(name = "kra_state")
	private String kraState;

	@Column(name = "kra_per_address_1")
	private String kraPerAddress1;

	@Column(name = "kra_per_address_2")
	private String kraPerAddress2;

	@Column(name = "kra_per_address_3")
	private String kraPerAddress3;

	@Column(name = "kra_per_pin")
	private int kraPerPin;

	@Column(name = "kra_per_city")
	private String kraPerCity;

	@Column(name = "kra_per_state")
	private String kraPerState;
}

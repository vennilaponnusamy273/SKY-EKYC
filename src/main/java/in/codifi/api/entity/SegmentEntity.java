package in.codifi.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_segment_details")
@Getter
@Setter
public class SegmentEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "equity_cash")
	private int equCash;

	@Column(name = "mutual_funds")
	private int mutFunds;

	@Column(name = "equity_derivatives")
	private int ed;

	@Column(name = "currency_derivatives")
	private int cd;

	@Column(name = "commodity")
	private int comm;

	@Column(name = "aluminium")
	private String aluminium;

	@Column(name = "brass")
	private String brass;

	@Column(name = "cardamom")
	private String cardamom;

	@Column(name = "copper")
	private String copper;

	@Column(name = "cotton")
	private String cotton;

	@Column(name = "crupalmoil")
	private String crupalmoil;

	@Column(name = "crudeoil")
	private String crudeoil;

	@Column(name = "gold")
	private String gold;

	@Column(name = "menthaoil")
	private String menthaoil;

	@Column(name = "naturalgas")
	private String naturalgas;

	@Column(name = "nickel")
	private String nickel;

	@Column(name = "pepper")
	private String pepper;

	@Column(name = "rbdpmolein")
	private String rbdpmolein;

	@Column(name = "silver")
	private String silver;

	@Column(name = "zinc")
	private String zinc;

	@Column(name = "kapas")
	private String kapas;

	@Column(name = "rubber")
	private String rubber;

	@Column(name = "mcxbulldex")
	private String mcxbulldex;

	@Column(name = "mcxmetldex")
	private String mcxmetldex;

	@Column(name = "mcxcomdex")
	private String mcxcomdex;

	@Column(name = "leadsky")
	public String lead;
}

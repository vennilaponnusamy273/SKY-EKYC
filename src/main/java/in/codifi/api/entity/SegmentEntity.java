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

	@Column(name = "nse")
	private int nse;

	@Column(name = "nsemf")
	private int nsemf;

	@Column(name = "nfo")
	private int nfo;

	@Column(name = "cds")
	private int cds;

	@Column(name = "mcx")
	private int mcx;

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

	@Column(name = "mcxbullder")
	private String mcxbullder;

	@Column(name = "mcxmetldex")
	private String mcxmetldex;

	@Column(name = "mcxxomdex")
	private String mcxxomdex;

	@Column(name = "leadsky")
	public String leadsky;
}

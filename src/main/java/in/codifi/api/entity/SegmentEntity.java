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

	@Column(name = "category")
	private String category;

	@Column(name = "consent")
	private int consent;

}

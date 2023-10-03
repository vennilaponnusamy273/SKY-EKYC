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
@Entity(name = "tbl_kra_keyvalue_pair")
public class KraKeyValueEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "master_id")
	private String masterId;

	@Column(name = "master_name")
	private String masterName;

	@Column(name = "data_key")
	private String kraKey;

	@Column(name = "data_value")
	private String kraValue;

}

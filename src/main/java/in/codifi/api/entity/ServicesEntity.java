package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_service_rules")
@Getter
@Setter
public class ServicesEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "service_name", length = 100)
	private String serviceName;

	@Column(name = "access_allowed", length = 6)
	private Integer accessAllowed;

	@Column(name = "order_value")
	private String orderValue;
}

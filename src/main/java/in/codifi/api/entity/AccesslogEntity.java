package in.codifi.api.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_ACCESS_LOG_DETAILS")
public class AccesslogEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "application_id", nullable = false)
	private Long applicationId;
	
	
	@Column(name = "uri")
	private String uri;
	
	@Column(name = "user_agent")
	private String user_agent;
	
	@Column(name = "device_ip")
	private String device_ip;
	
	@Column(name = "type")
	private String type;

	@Lob
	@Column(name = "response_data")
	private String response_data;
	
	@Lob
	@Column(name = "request_data")
	private String request_data;
}

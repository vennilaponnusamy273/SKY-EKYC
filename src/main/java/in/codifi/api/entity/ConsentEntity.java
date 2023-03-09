package in.codifi.api.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_consent")
public class ConsentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "consent_type", nullable = false)
	private String consentType;

	@Column(name = "text_to_user", nullable = false)
	private String textToUser;

	@Column(name = "accepted_time", nullable = false)
	private LocalDateTime acceptedTime;

	@Column(name = "user_agent")
	private String userAgent;

	@Column(name = "device_ip")
	private String deviceIp;

	@Column(name = "created_by", nullable = false)
	private String createdBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "created_on")
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
}

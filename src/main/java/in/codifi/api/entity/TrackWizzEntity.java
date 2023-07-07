package in.codifi.api.entity;

import java.io.Serializable;

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
@Entity(name = "tbl_track_wizz")
public class TrackWizzEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "trackwizz_req_id")
	private Long trackwizzReqId;

	@Lob
	@Column(name = "trackwizz_req")
	private String trackwizzReq;

	@Lob
	@Column(name = "trackwizz_res")
	private String trackwizzRes;

	@Lob
	@Column(name = "pass_key")
	private String passKey;

}

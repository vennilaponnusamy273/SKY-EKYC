package in.codifi.api.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;


@Entity(name = "tbl_user_status")
@Getter
@Setter
public class UserStatus extends CommonEntity implements Serializable {
	 
		private static final long serialVersionUID = 1L;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id")
		private Long id;

		@Column(name = "application_id", nullable = false)
		private Long applicationId;

		@Column(name = "stage")
		private String stage;
		

		@Column(name = "status")
		private String status;
		

		@Column(name = "attempt")
		private long attempt;
}

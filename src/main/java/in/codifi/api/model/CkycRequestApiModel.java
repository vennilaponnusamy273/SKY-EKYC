package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CkycRequestApiModel {

	private String id_type;
	private String id_num;
	private String full_name;
	private String gender;
	private String dob;
	private String req_id;

}
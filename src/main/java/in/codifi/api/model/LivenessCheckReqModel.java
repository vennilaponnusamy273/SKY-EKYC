package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivenessCheckReqModel {

	private String doc_base64;
	private Long req_id;

}

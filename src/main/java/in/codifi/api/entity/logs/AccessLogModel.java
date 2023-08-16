package in.codifi.api.entity.logs;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessLogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String applicationId;
	private String uri;
	private String method;
	private String reqId;
	private String reqBody;
	private String resBody;
	private String userAgent;
	private String deviceIp;
	private String contentType;
	private String session;

}

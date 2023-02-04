package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseModel {
	private int stat;
	private int page;
	private String message;
	private String reason;
	private Object result;
}

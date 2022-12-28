package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseModel {
	private String stat;
	private String message;
	private Object result;
}

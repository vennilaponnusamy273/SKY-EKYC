package in.codifi.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserAttribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("mobile")
	private List<String> mobile;
	@JsonProperty("pan")
	private List<String> pan;
	@JsonProperty("ucc")
	private List<String> ucc;

}

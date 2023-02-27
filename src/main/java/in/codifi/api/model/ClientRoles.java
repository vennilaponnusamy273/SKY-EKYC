package in.codifi.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRoles {

	@JsonProperty("app-vue")
	private List<String> clientId;

}

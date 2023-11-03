package in.codifi.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioRulesData {
	@JsonProperty("strict_validation_types")
	private List<String> strictValidationTypes;
}

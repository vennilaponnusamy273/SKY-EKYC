package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RejectionModel {
	private String rejectedPage;
	private String rejectedReason;
	private String docType;
}

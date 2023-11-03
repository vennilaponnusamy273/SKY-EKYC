package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioResponseAction {
	@JsonProperty("id")
	private String id;
	@JsonProperty("type")
	private String type;
	@JsonProperty("status")
	private String status;
	@JsonProperty("execution_request_id")
	private String executionRequestId;
	@JsonProperty("details")
	private DigioResponseDetails details;
	@JsonProperty("validation_result")
	private DigioValidationResult validationResult;
	@JsonProperty("completed_at")
	private String completedAt;
	@JsonProperty("face_match_obj_type")
	private String faceMatchObjType;
	@JsonProperty("face_match_status")
	private String faceMatchStatus;
	@JsonProperty("obj_analysis_status")
	private String objAnalysisStatus;
	@JsonProperty("processing_done")
	private Boolean processingDone;
	@JsonProperty("retry_count")
	private Integer retryCount;
	@JsonProperty("rules_data")
	private DigioRulesData rulesData;
}

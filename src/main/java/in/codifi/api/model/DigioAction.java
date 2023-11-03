package in.codifi.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigioAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("type")
	private String type;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("strict_validation_type")
	private String strictValidationType;
	@JsonProperty("id_analysis_required")
	private Boolean idAnalysisRequired;
	@JsonProperty("allow_ocr_data_update")
	private Boolean allowOcrDataUpdate;
	@JsonProperty("face_match_obj_type")
	private String faceMatchObjType;
	@JsonProperty("document_types")
	private List<String> documentTypes;

}

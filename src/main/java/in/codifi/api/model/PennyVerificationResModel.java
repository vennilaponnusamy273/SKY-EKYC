package in.codifi.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PennyVerificationResModel {

	
	
	@JsonProperty("id")
    private String id;
	
    @JsonProperty("verified")
    private String verified;

   
    @JsonProperty("verified_at")
    private String verifiedAt;

    @JsonProperty("beneficiary_name_with_bank")
    private String beneficiaryNameWithBank;

//    @JsonProperty("fuzzy_match_result")
//    private String fuzzyMatchResult;
//
//    @JsonProperty("fuzzy_match_score")
//    private int fuzzyMatchScore;
    
}

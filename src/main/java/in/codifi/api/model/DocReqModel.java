package in.codifi.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocReqModel {
	private boolean isPanRequired = true;
	private boolean isSignRequired = true;
	private boolean isIncomeProofRequired = true;
	private boolean isChequeRequired = true;
	private boolean isNameMismatch = true;
	private List<String> proofTypes;
}

package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocReqModel {
	private boolean isPanRequired = true;
	private boolean isSignRequired = true;
	private boolean isIncomeProofRequired= true;
	private boolean isChequeRequired = true;
	private boolean isNameMismatch= true;
}

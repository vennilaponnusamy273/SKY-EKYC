package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentCheckModel {

	private boolean panCardPresent;
	private String panName;
	private boolean signaturePresent;
	private String signName;
	private boolean incomeProofPresent;
	private String incomeProofName;
	private String incomeProofType;
	private boolean cancelledChequeOrStatement;
	private String checqueName;
	private boolean ipvPresent;
	private String ipvUrl;
	private String imageUrl;

}

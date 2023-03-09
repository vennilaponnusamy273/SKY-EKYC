package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentCheckModel {

	private boolean panCardPresent;
	private boolean signaturePresent;
	private boolean incomeProofPresent;
	private boolean cancelledChequeOrStatement;
	private boolean ipvPresent;
	private String imageUrl;

}

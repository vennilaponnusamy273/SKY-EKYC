package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentCheckModel {

	private boolean panCardPresent;
	private String panName;
	private String panUrl;
	private boolean signaturePresent;
	private String signName;
	private String signUrl;
	private boolean incomeProofPresent;
	private String incomeProofName;
	private String incomeProofUrl;
	private boolean cancelledChequeOrStatement;
	private String checqueName;
	private String checqueUrl;
	private boolean ipvPresent;
	private String ipvUrl;
	private String imageUrl;

}

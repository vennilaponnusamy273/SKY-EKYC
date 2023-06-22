package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfApplicationDataModel {
	
	private int id;
	private long applicationNo;
	private String sebiRegNo;
	private String cdslDpRegNo;
	private String clientName;
	private String clientCode; 
	private String branch;

}

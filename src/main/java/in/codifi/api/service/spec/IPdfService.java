package in.codifi.api.service.spec;

import java.io.IOException;

import javax.ws.rs.core.Response;

import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;

public interface IPdfService {

	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 * @throws IOException 
	 */
	Response savePdf(long applicationId,int activeStatus) throws IOException;

	/**
	 * Method to generate Esign
	 * 
	 * @author Pradeep
	 * @param pdfModel
	 * @return
	 */
	ResponseModel generateEsign(PdfApplicationDataModel pdfModel);
	
	/**
	 * Method to re direct from NSDL
	 * 
	 * @author prade
	 */
	Response getNsdlXml(String msg);

}

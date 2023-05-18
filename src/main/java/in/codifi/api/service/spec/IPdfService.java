package in.codifi.api.service.spec;

import javax.ws.rs.core.Response;

import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;

public interface IPdfService {

	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	Response savePdf(long applicationId);

	/**
	 * Method to generate Esign
	 * 
	 * @author Pradeep
	 * @param pdfModel
	 * @return
	 */
	ResponseModel generateEsign(PdfApplicationDataModel pdfModel);
}

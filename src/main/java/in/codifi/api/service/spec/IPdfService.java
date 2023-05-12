package in.codifi.api.service.spec;

import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;

public interface IPdfService {

	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	ResponseModel savePdf(PdfApplicationDataModel pdfModel);

	/**
	 * Method to save PDF data coordinates
	 * 
	 * @author gowthaman
	 * @return
	 */
	ResponseModel saveDataCoordinates();

}

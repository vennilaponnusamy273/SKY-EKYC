package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IPdfController;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IPdfService;

@Path("/pdf")
public class PdfController implements IPdfController {

	@Inject
	IPdfService iPdfService;

	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	public ResponseModel savePdf(PdfApplicationDataModel pdfModel) {
		ResponseModel responseModel = new ResponseModel();
		if (pdfModel.getApplicationNo() > 0) {
			iPdfService.savePdf(pdfModel);
		}
		return responseModel;
	}

	/**
	 * Method to save PDF data coordinates
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	public ResponseModel saveDataCoordinates() {
		ResponseModel responseModel = new ResponseModel();
		iPdfService.saveDataCoordinates();
		return responseModel;
	}

}

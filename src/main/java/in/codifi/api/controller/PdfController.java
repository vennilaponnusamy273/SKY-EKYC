package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import in.codifi.api.controller.spec.IPdfController;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IPdfService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/pdf")
public class PdfController implements IPdfController {

	@Inject
	IPdfService iPdfService;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	public Response savePdf(long applicationId) {
		if (applicationId > 0) {
			return iPdfService.savePdf(applicationId);
		} else {
			if (applicationId <= 0) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.USER_ID_NULL)
						.build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.PARAMETER_NULL)
						.build();
			}
		}
	}

	/**
	 * Method to save PDF data coordinates
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	public ResponseModel generateEsign(PdfApplicationDataModel pdfModel) {
		ResponseModel responseModel = new ResponseModel();
		if (pdfModel != null && pdfModel.getApplicationNo() > 0) {
			responseModel = iPdfService.generateEsign(pdfModel);
		} else {
			if (pdfModel != null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to re direct from NSDL
	 */
	public Response getNsdlXml(String msg) {
		if (StringUtil.isNotNullOrEmpty(msg)) {
			return iPdfService.getNsdlXml(msg);
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.XML_MSG_NULL).build();
		}
	}
}

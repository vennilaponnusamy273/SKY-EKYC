package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IDocUpController;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IDocumentService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/doc")
public class DocUpController implements IDocUpController {
	@Inject
	IDocumentService docservice;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to Upload proof Documnt
	 */
	@Override
	public ResponseModel uploadDoc(FormDataModel fileModel) {
		ResponseModel response = new ResponseModel();
		if (fileModel != null && fileModel.getApplicationId() > 0) {
			response = docservice.uploadDoc(fileModel);
		} else {
			if (fileModel == null) {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		}
		return response;
	}

	/**
	 * Method to Upload Ivr Proof Details
	 */
	@Override
	public ResponseModel uploadIvr(IvrModel ivrModel) {
		ResponseModel response = new ResponseModel();
		if (ivrModel != null && ivrModel.getApplicationId() > 0) {
			response = docservice.uploadIvr(ivrModel);
		} else {
			if (ivrModel == null) {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		}
		return response;
	}

}

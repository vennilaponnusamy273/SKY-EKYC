package in.codifi.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IDocUpController;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IDocumentService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
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
		List<String> docType = new ArrayList<>(Arrays.asList(EkycConstants.DOC_CHEQUE, EkycConstants.DOC_PAN,
				EkycConstants.DOC_INCOME, EkycConstants.DOC_SIGNATURE));
		if (fileModel != null && fileModel.getApplicationId() > 0 && docType.contains(fileModel.getDocumentType())) {
			response = docservice.uploadDoc(fileModel);
		} else {
			if (fileModel == null) {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else if (!docType.contains(fileModel.getDocumentType())) {
				response = commonMethods.constructFailedMsg(MessageConstants.WRONG_DOCUMENT);
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

	/**
	 * Method to generate IVR Link
	 */
	@Override
	public ResponseModel getIvrLink(@NotNull long applicationId) {
		ResponseModel response = new ResponseModel();
		if (applicationId > 0) {
			response = docservice.getLinkIvr(applicationId);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return response;
	}

	/**
	 * Method to check document present or not
	 */
	@Override
	public ResponseModel getDocument(@NotNull long applicationId) {
		ResponseModel response = new ResponseModel();
		if (applicationId > 0) {
			response = docservice.getDocument(applicationId);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return response;
	}
}

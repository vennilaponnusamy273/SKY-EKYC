package in.codifi.api.controller;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IEsignGeneratorController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IEsignGeneratorService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/esign")
public class EsignGeneratorController implements IEsignGeneratorController {

	@Inject
	IEsignGeneratorService esignGeneratorService;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to get xml for E sign
	 * 
	 */
	@Override
	public ResponseModel xmlGenerator(@NotNull long applicationId) {
		ResponseModel response = new ResponseModel();
		if (applicationId > 0) {
			response = esignGeneratorService.xmlGenerator(applicationId);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return response;
	}

}

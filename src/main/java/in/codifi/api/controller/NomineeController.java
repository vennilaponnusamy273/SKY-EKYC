package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.INomineeController;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.NomineeDocModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.INomineeService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/nominee")
public class NomineeController implements INomineeController {
	@Inject
	INomineeService service;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to get nominee and Guardian Details
	 */
	@Override
	public ResponseModel getNominee(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = service.getNominee(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to upload Nominee details
	 */
	@Override
	public ResponseModel uploadNominee(NomineeDocModel fileModel) {
		ResponseModel response = new ResponseModel();
		response = service.uploadDocNominee(fileModel);
		return response;
	}

	/**
	 * Method to delete Nominee
	 */
	@Override
	public ResponseModel deleteNom(long id) {
		ResponseModel response = new ResponseModel();
		response = service.deleteNom(id);
		return response;
	}

	/**
	 * Method to update Nominee Alloction
	 */
	@Override
	public ResponseModel updateNomineeAllocation(NomineeEntity nomineeEntity) {
		ResponseModel response = new ResponseModel();
		if (nomineeEntity.getApplicationId() > 0) {
			response = service.updateNomineeAllocation(nomineeEntity);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return response;
	}

}

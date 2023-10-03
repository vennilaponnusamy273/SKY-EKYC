package in.codifi.api.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IIvrController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.service.spec.IIvrService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/ivr")
public class IvrController implements IIvrController {
	@Inject
	CommonMethods commonMethods;
	@Inject
	IIvrService iIvrService;
	@Inject
	ApplicationUserRepository applicationUserRepository;

	/**
	 * Method to get nominee and Guardian Details
	 */
	@Override
	public ResponseModel getIvr(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = iIvrService.getIvr(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to Upload Ivr Proof Details
	 */
	@Override
	public ResponseModel uploadIvr(IvrModel ivrModel) {
		ResponseModel response = new ResponseModel();
		if (ivrModel != null && ivrModel.getApplicationId() > 0) {
			response = iIvrService.uploadIvr(ivrModel);
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
			response = iIvrService.getIvrLink(applicationId);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return response;
	}

	/**
	 * Method to send link to mail or sms
	 */

	@Override
	public ResponseModel sendLink(long applicationId, String type) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0 && StringUtil.isNotNullOrEmpty(type)) {
			Optional<ApplicationUserEntity> isUserPresent = applicationUserRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				responseModel = iIvrService.sendLink(isUserPresent.get(), type);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} else {
			if (StringUtil.isNullOrEmpty(type)) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.LINK_TYPE_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		}
		return responseModel;
	}

}

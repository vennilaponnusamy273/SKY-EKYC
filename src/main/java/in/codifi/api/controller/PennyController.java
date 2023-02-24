package in.codifi.api.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IPennyController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.filter.MyFilter;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.service.spec.IPennyService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Path("/penny")
public class PennyController implements IPennyController {
	@Inject
	ApplicationUserRepository userRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	IPennyService iPennyService;
	@Inject
	MyFilter filter;

	/**
	 * Method to Create Contact for penny drop
	 */
	@Override
	public ResponseModel createContact(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				responseModel = iPennyService.createContact(isUserPresent.get());
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		filter.Access_Req_Res_Save_object(applicationId,responseModel,EkycConstants.CREATE_CONTACT,applicationId);
		return responseModel;
	}

	/**
	 * Method to add Account in created Contact
	 */
	@Override
	public ResponseModel addAccount(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				responseModel = iPennyService.addAccount(isUserPresent.get());
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		filter.Access_Req_Res_Save_object(applicationId,responseModel,EkycConstants.ADD_ACOUNT,applicationId);
		return responseModel;
	}

	/**
	 * Method to put some penny Amount
	 */
	@Override
	public ResponseModel createPayout(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				responseModel = iPennyService.createPayout(isUserPresent.get());
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		filter.Access_Req_Res_Save_object(applicationId,responseModel,EkycConstants.CREATE_PAYOUT,applicationId);
		return responseModel;
	}

	/**
	 * Method to Validate Penny Details
	 */
	@Override
	public ResponseModel ValidateDetails(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				responseModel = iPennyService.ValidateDetails(isUserPresent.get());
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

}

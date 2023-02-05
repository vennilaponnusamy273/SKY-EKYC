package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IUserController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.helper.DigilockerHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IUserService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/user")
public class UserController implements IUserController {
	@Inject
	CommonMethods commonMethods;
	@Inject
	IUserService iUserService;
	@Inject
	DigilockerHelper digilockerHelper;

	/**
	 * test Method
	 */
	public ResponseModel test() {
		ResponseModel model = new ResponseModel();
		model.setMessage("test");
		return model;
	}

	/**
	 * Method to send otp to mobile number
	 */
	@Override
	public ResponseModel sendSmsOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && userEntity.getMobileNo() != null && userEntity.getMobileNo() > 0) {
			responseModel = iUserService.sendSmsOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to validate Sms OTP
	 */
	@Override
	public ResponseModel verifySmsOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && userEntity.getMobileNo() != null && userEntity.getMobileNo() > 0) {
			responseModel = iUserService.verifySmsOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to send otp to email
	 */
	@Override
	public ResponseModel sendMailOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getEmailId()) && userEntity.getId() > 0) {
			responseModel = iUserService.sendMailOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				if (userEntity.getId() <= 0) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_NULL);
				}
			}
		}
		return responseModel;

	}

	/**
	 * Method to validate email OTP
	 */
	@Override
	public ResponseModel verifyEmailOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getEmailId())) {
			responseModel = iUserService.verifyEmailOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to save pan id to get details
	 */
	@Override
	public ResponseModel getPanDetails(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getPanNumber()) && userEntity.getId() > 0) {
			responseModel = iUserService.getPanDetails(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				if (userEntity.getId() <= 0) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.PAN_NUMBER_NULL);
				}
			}
		}
		return responseModel;
	}

	/**
	 * Method to save Date Of Birth
	 */
	@Override
	public ResponseModel saveDob(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getDob()) && userEntity.getId() > 0) {
			responseModel = iUserService.saveDob(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				if (userEntity.getId() <= 0) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.PAN_NUMBER_NULL);
				}
			}
		}
		return responseModel;
	}

	/**
	 * Method to intialize digilocker
	 * 
	 * @return
	 */
	@Override
	public ResponseModel iniDigilocker(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = iUserService.iniDigilocker(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to save address from digi
	 */
	@Override
	public ResponseModel saveDigi(String code, String state, long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (StringUtil.isNotNullOrEmpty(code) && StringUtil.isNotNullOrEmpty(state) && applicationId > 0) {
			responseModel = digilockerHelper.saveDigi(code, state, applicationId);
		} else {
			if (StringUtil.isNullOrEmpty(code)) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.DIGI_CODE_NULL);
			} else if (applicationId <= 0) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.DIGI_STATE_NULL);
			}
		}
		return responseModel;
	}
}

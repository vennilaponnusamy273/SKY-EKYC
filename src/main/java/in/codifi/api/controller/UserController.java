package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IUserController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.filter.MyFilter;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.service.spec.IUserService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/user")
public class UserController implements IUserController {
	@Inject
	CommonMethods commonMethods;
	@Inject
	IUserService iUserService;

	@Inject 
	ApplicationUserRepository applicationUserRepository;
	
	@Inject
	MyFilter filter;
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
	@SuppressWarnings("unused")
	@Override
	public ResponseModel verifySmsOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		ApplicationUserEntity getUserIdfromMobileNo=applicationUserRepository.findByMobileNo(userEntity.getMobileNo());
		if (userEntity != null && userEntity.getMobileNo() != null && userEntity.getMobileNo() > 0) {
			responseModel = iUserService.verifySmsOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_NULL);
			}
		}
		filter.Access_Req_Res_Save_object(userEntity, responseModel,EkycConstants.SMS_VERIFY,getUserIdfromMobileNo.getId());
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
		filter.Access_Req_Res_Save_object(userEntity,responseModel,EkycConstants.EMAIL,userEntity.getId());
		return responseModel;

	}

	/**
	 * Method to validate email OTP
	 */
	@SuppressWarnings("unused")
	@Override
	public ResponseModel verifyEmailOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		ApplicationUserEntity getUserIdfromEmail=applicationUserRepository.findByEmailId(userEntity.getEmailId());
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getEmailId())) {
			responseModel = iUserService.verifyEmailOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_NULL);
			}
		}
		filter.Access_Req_Res_Save_object(userEntity,responseModel,EkycConstants.EMAIL_VERIFY,getUserIdfromEmail.getId());
		return responseModel;
	}

	/**
	 * Method to get User Details
	 */
	@Override
	public ResponseModel getUserDetailsByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = iUserService.getUserDetailsById(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to Update Stage to skip
	 */
	@Override
	public ResponseModel updateStage(long applicationId, double stage) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			commonMethods.UpdateStep(stage, applicationId);
			responseModel = iUserService.getUserDetailsById(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}
	@Override
	public ResponseModel getBankStatementStatus(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = iUserService.BankStatementCheck(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}
}

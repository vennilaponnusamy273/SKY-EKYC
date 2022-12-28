package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IEmailController;
import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IEmailService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/email")
public class EmailController implements IEmailController {

	@Inject
	CommonMethods commonMethods;
	@Inject
	IEmailService emailService;

	/**
	 * test Method
	 */
	public ResponseModel test() {
		commonMethods.test();
		commonMethods.sendOTPtoMobile(1234, 8526707787L);
		ResponseModel model = new ResponseModel();
		model.setMessage("test");
		return model;
	}

	/**
	 * Method to send otp to email
	 */
	@Override
	public ResponseModel sendMailOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getEmailId()) && userEntity.getId() > 0) {
			responseModel = emailService.sendMailOtp(userEntity);
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
	public ResponseModel verifyEmailOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getEmailId())) {
			responseModel = emailService.verifyEmailOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_NULL);
			}
		}
		return responseModel;
	}

}

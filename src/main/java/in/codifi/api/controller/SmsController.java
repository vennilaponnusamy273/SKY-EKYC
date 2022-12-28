package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.ISmsController;
import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.ISmsService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/sms")
public class SmsController implements ISmsController {

	@Inject
	ISmsService iSmsService;

	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to send otp to mobile number
	 */
	@Override
	public ResponseModel sendSmsOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && userEntity.getMobileNo() != null && userEntity.getMobileNo() > 0) {
			responseModel = iSmsService.sendSmsOtp(userEntity);
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
	public ResponseModel verifySmsOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && userEntity.getMobileNo() != null && userEntity.getMobileNo() > 0
				&& userEntity.getSmsOtp() > 0) {
			responseModel = iSmsService.verifySmsOtp(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_NULL);
			}
		}
		return responseModel;
	}

}

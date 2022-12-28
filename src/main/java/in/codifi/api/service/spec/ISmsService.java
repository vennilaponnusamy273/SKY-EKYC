package in.codifi.api.service.spec;

import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;

public interface ISmsService {
	/**
	 * Method to send otp to mobile number
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel sendSmsOtp(UserEntity userEntity);

	/**
	 * Method to validate sms OTP
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel verifySmsOtp(UserEntity userEntity);

}
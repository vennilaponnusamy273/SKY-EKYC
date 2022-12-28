package in.codifi.api.service.spec;

import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;

public interface IEmailService {
	/**
	 * Method to send otp to email
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel sendMailOtp(UserEntity userEntity);

	/**
	 * Method to validate email OTP
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel verifyEmailOtp(UserEntity userEntity);

}

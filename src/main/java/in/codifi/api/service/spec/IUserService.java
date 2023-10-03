package in.codifi.api.service.spec;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;

public interface IUserService {
	/**
	 * Method to send otp to mobile number
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel sendSmsOtp(ApplicationUserEntity userEntity);

	/**
	 * Method to validate sms OTP
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel verifySmsOtp(ApplicationUserEntity userEntity);

	/**
	 * Method to send otp to email
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel sendMailOtp(ApplicationUserEntity userEntity);

	/**
	 * Method to validate email OTP
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel verifyEmailOtp(ApplicationUserEntity userEntity);

	/**
	 * Method to get User details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel getUserDetailsById(long applicationId);

	/**
	 * Method to get Documents that need to upload
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */

	ResponseModel docStatus(long applicationId);

	/**
	 * Method to create new user in keycloak
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel userCreation(ApplicationUserEntity userEntity);

	/**
	 * Method to star over the application
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel startOver(ApplicationUserEntity applicationUserEntity);
}

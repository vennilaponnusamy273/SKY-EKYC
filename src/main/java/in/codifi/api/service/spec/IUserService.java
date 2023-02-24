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
	 * Method to get Bank Statement details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	

	ResponseModel BankStatementCheck(long applicationId);
}

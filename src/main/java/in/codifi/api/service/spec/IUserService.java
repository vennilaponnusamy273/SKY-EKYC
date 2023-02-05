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
	 * Method to get pan details from NSDL
	 * 
	 * @author Vennila Ponnusamy
	 * @param PanCardDetails
	 * @return
	 */

	ResponseModel getPanDetails(ApplicationUserEntity pan);

	/**
	 * Method to save Date of birth
	 * 
	 * @author prade
	 * @param PanCardDetails
	 * @return
	 */

	ResponseModel saveDob(ApplicationUserEntity userEntity);

	/**
	 * Method to intialize digi locker
	 * 
	 * @author prade
	 * @param PanCardDetails
	 * @return
	 */
	ResponseModel iniDigilocker(long applicationId);
}

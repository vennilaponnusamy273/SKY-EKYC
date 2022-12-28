package in.codifi.api.service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.UserRepository;
import in.codifi.api.service.spec.ISmsService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Service
public class SmsService implements ISmsService {
	@Inject
	UserRepository repository;

	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationProperties props;

	/**
	 * Method to send otp to mobile number
	 */
	public ResponseModel sendSmsOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			UserEntity updatedUserDetails = null;
			UserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			if (oldUserEntity == null) {
				// send OTP
				int otp = commonMethods.generateOTP(userEntity.getMobileNo());
				commonMethods.sendOTPtoMobile(otp, userEntity.getMobileNo());
				userEntity.setSmsOtp(otp);
				userEntity.setSmsOtpTimeStamp(Instant.now().toEpochMilli());
				updatedUserDetails = repository.save(userEntity);
			} else {
				// resend OTP
				int otp = commonMethods.generateOTP(userEntity.getMobileNo());
				commonMethods.sendOTPtoMobile(otp, userEntity.getMobileNo());
				oldUserEntity.setSmsOtp(otp);
				oldUserEntity.setSmsOtpTimeStamp(Instant.now().toEpochMilli());
				oldUserEntity.setSmsVerified(0);
				updatedUserDetails = repository.save(oldUserEntity);
			}
			if (updatedUserDetails != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setResult(updatedUserDetails);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_GENERATE_OTP);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	

	/**
	 * Method to validate Sms OTP
	 */
	@Override
	public ResponseModel verifySmsOtp(UserEntity userEntity) {
		ResponseModel responseModel = null;
		try {
			UserEntity updatedUserDetails = null;
			UserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			long currentTime = Instant.now().toEpochMilli();
			if (oldUserEntity != null) {
				long nowPlus5Minutes = oldUserEntity.getSmsOtpTimeStamp() + TimeUnit.MINUTES.toMillis(5);
				if (oldUserEntity.getSmsOtp() == userEntity.getSmsOtp() && currentTime < nowPlus5Minutes) {
					oldUserEntity.setSmsVerified(1);
					updatedUserDetails = repository.save(oldUserEntity);
					if (updatedUserDetails != null) {
						responseModel = new ResponseModel();
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setResult(updatedUserDetails);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_VERIFY_OTP);
					}
				} else {
					if (currentTime > nowPlus5Minutes) {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.OTP_TIME_EXPIRED);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
					}
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_WRONG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
}

package in.codifi.api.service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.UserRepository;
import in.codifi.api.service.spec.IEmailService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Service
public class EmailService implements IEmailService {
	@Inject
	UserRepository repository;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to send otp to email
	 */
	@Override
	public ResponseModel sendMailOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			UserEntity updatedUserDetails = null;
			Optional<UserEntity> isUserPresent = repository.findById(userEntity.getId());
			if (isUserPresent.isPresent()) {
				UserEntity oldUserEntity = isUserPresent.get();
				int otp = commonMethods.generateOTP(oldUserEntity.getMobileNo());
				commonMethods.sendMailOtp(otp, userEntity.getEmailId());
				oldUserEntity.setEmailId(userEntity.getEmailId());
				oldUserEntity.setEmailOtp(otp);
				oldUserEntity.setEmailOtpTimeStamp(Instant.now().toEpochMilli());
				oldUserEntity.setEmailVerified(0);
				updatedUserDetails = repository.save(oldUserEntity);
				if (updatedUserDetails != null) {
					responseModel = new ResponseModel();
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setResult(updatedUserDetails);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_GENERATE_OTP);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.WRONG_USER_ID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to validate email OTP
	 */
	@Override
	public ResponseModel verifyEmailOtp(UserEntity userEntity) {
		ResponseModel responseModel = null;
		try {
			UserEntity updatedUserDetails = null;
			UserEntity oldUserEntity = repository.findByEmailId(userEntity.getEmailId());
			long currentTime = Instant.now().toEpochMilli();
			if (oldUserEntity != null) {
				long nowPlus5Minutes = oldUserEntity.getEmailOtpTimeStamp() + TimeUnit.MINUTES.toMillis(5);
				if (oldUserEntity.getEmailOtp() == userEntity.getEmailOtp() && currentTime < nowPlus5Minutes) {
					oldUserEntity.setEmailVerified(1);
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
				responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_WRONG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

}

package in.codifi.api.helper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.model.ApplicationMasterModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.service.BankService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class UserHelper {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	AddressRepository addressRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	SmsRestService smsRestService;

	private static final Logger logger = LogManager.getLogger(UserHelper.class);
	/**
	 * Method to save User Sms Otp in DB
	 * 
	 * @param otp
	 * @param userEntity
	 * @return
	 */
	public ApplicationUserEntity saveOrUpdateSmsTrigger(ApplicationUserEntity userEntity) {
		ApplicationUserEntity savedEntity=null;
		try {
		String mapKey = String.valueOf(userEntity.getMobileNo()) + EkycConstants.SMS_KEY;
		int otp = 0;
		otp = commonMethods.generateOTP(userEntity.getMobileNo());
		HazleCacheController.getInstance().getVerifyOtp().put(mapKey, otp, 3600, TimeUnit.SECONDS);
		userEntity.setSmsOtp(otp);
		userEntity.setSmsVerified(0);
		 savedEntity = repository.save(userEntity);
		smsRestService.sendOTPtoMobile(otp, userEntity.getMobileNo());
		// alice Blue OTP
//		commonMethods.sendOTPMessage(Integer.toString(otp), userEntity.getMobileNo().toString());
		
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return savedEntity;
	}

	/**
	 * Method to populate all records by Application Id
	 * 
	 * @param applicationId
	 * @return
	 */
	public ApplicationMasterModel populateAllRecord(long applicationId) {
		ApplicationMasterModel model = null;
		try {
		if (applicationId > 0) {
			model = new ApplicationMasterModel();
			Optional<ApplicationUserEntity> userEntity = repository.findById(applicationId);
			ProfileEntity profileEntity = profileRepository.findByapplicationId(applicationId);
			AddressEntity addressEntity = addressRepository.findByapplicationId(applicationId);
			BankEntity bankEntity = bankRepository.findByapplicationId(applicationId);
			if (userEntity.isPresent()) {
				model.setApplicationMasterEntity(userEntity.get());
			}
			if (profileEntity != null) {
				model.setProfileEntity(profileEntity);
			}
			if (profileEntity != null) {
				model.setAddressEntity(addressEntity);
			}
			if (bankEntity != null) {
				model.setBankEntity(bankEntity);
			}
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return model;
	}

	/**
	 * Method to validate sending OTP to client 30 seconds for 5 times after that 5
	 * min interval
	 * 
	 * @param mapKey
	 * @return
	 */
	public ResponseModel checkOtpTimeValidation(String mapKey) {
		ResponseModel response = null;
try {
		long seconds = 0;
		if ((HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)
				&& HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 9)
				|| HazleCacheController.getInstance().getResendOtp().containsKey(mapKey)
						&& HazleCacheController.getInstance().getResendOtp().get(mapKey) > 4) {
			if (HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)
					&& HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 9) {
				long expiryTime = HazleCacheController.getInstance().getRetryOtp().getEntryView(mapKey)
						.getExpirationTime();
				seconds = (expiryTime - System.currentTimeMillis()) / 1000;
				response = commonMethods
						.constructFailedMsg(MessageConstants.RETRY_OTP_TRY_AFTER + seconds + MessageConstants.SECONDS);
			} else {
				long expiryTime = HazleCacheController.getInstance().getResendOtp().getEntryView(mapKey)
						.getExpirationTime();
				seconds = (expiryTime - System.currentTimeMillis()) / 1000;
				response = commonMethods
						.constructFailedMsg(MessageConstants.RETRY_OTP_TRY_AFTER + seconds + MessageConstants.SECONDS);
			}
		} else if (HazleCacheController.getInstance().getResendOtp().containsKey(mapKey)
				|| HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)) {
			// resend sms check
			if (HazleCacheController.getInstance().getResendOtp().containsKey(mapKey))
				if (HazleCacheController.getInstance().getResendOtp().get(mapKey) == 4)
					HazleCacheController.getInstance().getResendOtp().put(mapKey,
							HazleCacheController.getInstance().getResendOtp().get(mapKey) + 1, 30, TimeUnit.SECONDS);
				else
					HazleCacheController.getInstance().getResendOtp().put(mapKey,
							HazleCacheController.getInstance().getResendOtp().get(mapKey) + 1);
			else
				HazleCacheController.getInstance().getResendOtp().put(mapKey, 1);
			// retry sms check
			if (HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey))
				if (HazleCacheController.getInstance().getRetryOtp().get(mapKey) == 9)
					HazleCacheController.getInstance().getRetryOtp().put(mapKey,
							HazleCacheController.getInstance().getRetryOtp().get(mapKey) + 1, 300, TimeUnit.SECONDS);
				else
					HazleCacheController.getInstance().getRetryOtp().put(mapKey,
							HazleCacheController.getInstance().getRetryOtp().get(mapKey) + 1);
			else
				HazleCacheController.getInstance().getRetryOtp().put(mapKey, 1);
		} else {
			HazleCacheController.getInstance().getRetryOtp().put(mapKey, 1);
			HazleCacheController.getInstance().getResendOtp().put(mapKey, 1);

		}
} catch (Exception e) {
	logger.error("An error occurred: " + e.getMessage());
	response = commonMethods.constructFailedMsg(e.getMessage());
}
		return response;
	}

	public ResponseModel varifyOtpValidation(String mapKey) {
		ResponseModel response = null;
		try {
		long seconds = 0;
		if ((HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)
				&& HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 4)
				|| HazleCacheController.getInstance().getResendOtp().containsKey(mapKey)) {
			if (HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 4) {
				long expiryTime = HazleCacheController.getInstance().getRetryOtp().getEntryView(mapKey)
						.getExpirationTime();
				seconds = (expiryTime - System.currentTimeMillis()) / 1000;
			} else {
				long expiryTime = HazleCacheController.getInstance().getResendOtp().getEntryView(mapKey)
						.getExpirationTime();
				seconds = (expiryTime - System.currentTimeMillis()) / 1000;
			}
			response = commonMethods
					.constructFailedMsg(MessageConstants.RETRY_OTP_TRY_AFTER + seconds + MessageConstants.SECONDS);
		} else if (HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)) {
			if (HazleCacheController.getInstance().getRetryOtp().get(mapKey) == 4) {
				HazleCacheController.getInstance().getRetryOtp().put(mapKey,
						HazleCacheController.getInstance().getRetryOtp().get(mapKey) + 1, 300, TimeUnit.SECONDS);
			} else {
				HazleCacheController.getInstance().getRetryOtp().put(mapKey,
						HazleCacheController.getInstance().getRetryOtp().get(mapKey) + 1);
			}
		} else {
			HazleCacheController.getInstance().getRetryOtp().put(mapKey, 1);
			HazleCacheController.getInstance().getResendOtp().put(mapKey, 1, 30, TimeUnit.SECONDS);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

}

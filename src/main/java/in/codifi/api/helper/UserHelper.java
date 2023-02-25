package in.codifi.api.helper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

	/**
	 * Method to save User Sms Otp in DB
	 * 
	 * @param otp
	 * @param userEntity
	 * @return
	 */
	public ApplicationUserEntity saveOrUpdateSmsTrigger(int otp, ApplicationUserEntity userEntity) {
		String mapKey = String.valueOf(userEntity.getMobileNo()) + EkycConstants.SMS_KEY;
		userEntity.setSmsOtp(otp);
		userEntity.setSmsVerified(0);
		ApplicationUserEntity savedEntity = repository.save(userEntity);
		commonMethods.sendOTPtoMobile(otp, userEntity.getMobileNo());
		HazleCacheController.getInstance().getVerifyOtp().put(mapKey, otp, 300, TimeUnit.SECONDS);
		HazleCacheController.getInstance().getResendOtp().put(mapKey, otp, 30, TimeUnit.SECONDS);
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
		long seconds = 0;
		if ((HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)
				&& HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 4)
				|| HazleCacheController.getInstance().getResendOtp().containsKey(mapKey)) {
			if (HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)
					&& HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 4) {
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
		return response;
	}

	public ResponseModel varifyOtpValidation(String mapKey) {
		ResponseModel response = null;
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
		return response;
	}

}

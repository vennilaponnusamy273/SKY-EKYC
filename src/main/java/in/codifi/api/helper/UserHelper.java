package in.codifi.api.helper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.model.ApplicationMasterModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@Service
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

	public ApplicationUserEntity saveOrUpdateUser(int otp, ApplicationUserEntity userEntity) {
		String mapKey = String.valueOf(userEntity.getMobileNo()) + EkycConstants.SMS_KEY;
		userEntity.setSmsOtp(otp);
		userEntity.setSmsVerified(0);
		ApplicationUserEntity savedEntity = repository.save(userEntity);
		commonMethods.sendOTPtoMobile(otp, userEntity.getMobileNo());
		HazleCacheController.getInstance().getVerifyOtp().put(mapKey, otp, 300000, TimeUnit.MILLISECONDS);
		return savedEntity;
	}

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

}

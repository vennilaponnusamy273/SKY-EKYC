package in.codifi.api.helper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@Service
public class UserHelper {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	CommonMethods commonMethods;

	public ApplicationUserEntity saveOrUpdateUser(int otp, ApplicationUserEntity userEntity) {
		String mapKey = String.valueOf(userEntity.getMobileNo()) + EkycConstants.SMS_KEY;
		userEntity.setSmsOtp(otp);
		userEntity.setSmsVerified(0);
		ApplicationUserEntity savedEntity = repository.save(userEntity);
		commonMethods.sendOTPtoMobile(otp, userEntity.getMobileNo());
		HazleCacheController.getInstance().getVerifyOtp().put(mapKey, otp, 300000, TimeUnit.MILLISECONDS);
		return savedEntity;
	}

}

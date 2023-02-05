package in.codifi.api.service.spec;

import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.model.ResponseModel;

public interface IProfileService {
	/**
	 * Method to save Profile Details
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel saveProfile(ProfileEntity userEntity);

	/**
	 * Method to get Profile Details
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	ResponseModel getProfileByAppId(long applicationId);
}

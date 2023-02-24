package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IProfileController;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.filter.MyFilter;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IProfileService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Path("/profile")
public class ProfileController implements IProfileController {
	@Inject
	IProfileService iProfileService;
	@Inject
	CommonMethods commonMethods;

	@Inject
	MyFilter filter;
	/**
	 * Method to save Profile Details
	 */
	@Override
	public ResponseModel saveProfile(ProfileEntity profileEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (profileEntity != null && profileEntity.getApplicationId() > 0) {
			responseModel = iProfileService.saveProfile(profileEntity);
		} else {
			if (profileEntity != null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}
		}
		filter.Access_Req_Res_Save_object(profileEntity,responseModel,EkycConstants.PROFILE,profileEntity.getId());
		return responseModel;
	}

	/**
	 * Method to get Profile Details
	 */
	@Override
	public ResponseModel getProfileByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = iProfileService.getProfileByAppId(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

}

package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.controller.spec.IPennyController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.helper.RejectionStatusHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.service.spec.IProfileService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class ProfileService implements IProfileService {
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	IPennyController iPennyController;
	@Inject
	RejectionStatusHelper rejectionStatusHelper;
	private static final Logger logger = LogManager.getLogger(ProfileService.class);

	/**
	 * Method to save Profile Details
	 */
	@Override
	public ResponseModel saveProfile(ProfileEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ProfileEntity updatedEntity = null;
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(userEntity.getApplicationId());
			if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0) {
				ProfileEntity savedProfileEntity = profileRepository.findByapplicationId(userEntity.getApplicationId());
				if (savedProfileEntity != null) {
					userEntity.setId(savedProfileEntity.getId());
					updatedEntity = profileRepository.save(userEntity);
				} else {
					updatedEntity = profileRepository.save(userEntity);
				}
				rejectionStatusHelper.insertArchiveTableRecord(userEntity.getApplicationId(),
						EkycConstants.PAGE_PROFILE);
				if (updatedEntity != null && updatedEntity.getId() > 0) {
					commonMethods.UpdateStep(EkycConstants.PAGE_PROFILE, userEntity.getApplicationId());
					iPennyController.createContact(userEntity.getApplicationId());
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setResult(updatedEntity);
					responseModel.setPage(EkycConstants.PAGE_BANK);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_PROFILE);
				}
			} else {
				if (user.isEmpty()) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getApplicationId(), "ProfileService", "saveProfile", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In saveProfile for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to get Profile Details
	 */
	@Override
	public ResponseModel getProfileByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ProfileEntity savedProfileEntity = profileRepository.findByapplicationId(applicationId);
			if (savedProfileEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(savedProfileEntity);
				responseModel.setPage(EkycConstants.PAGE_PROFILE);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "ProfileService", "saveProfile", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In getProfileByAppId for the Error: "
							+ e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

}

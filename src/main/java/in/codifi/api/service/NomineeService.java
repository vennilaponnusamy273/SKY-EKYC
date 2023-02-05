package in.codifi.api.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.GuardianRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.service.spec.INomineeService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Service
public class NomineeService implements INomineeService {

	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	NomineeRepository nomineeRepository;
	@Inject
	GuardianRepository guardianRepository;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to save Nominee Details
	 */
	@Override
	public ResponseModel saveNominee(List<NomineeEntity> nomineeEntity) {
		ResponseModel responseModel = new ResponseModel();
		List<NomineeEntity> savedEntity = null;
		List<NomineeEntity> savedNomineeDetails = nomineeRepository
				.findByapplicationId(nomineeEntity.get(0).getApplicationId());
		if (StringUtil.isListNullOrEmpty(savedNomineeDetails)) {
			Optional<ApplicationUserEntity> user = applicationUserRepository
					.findById(nomineeEntity.get(0).getApplicationId());
			if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0) {
				for (NomineeEntity entity : nomineeEntity) {
					NomineeEntity savingNominee = nomineeRepository.save(entity);
					if (savingNominee != null && entity.getGuardianEntity() != null) {
						entity.getGuardianEntity().setNomineeId(savingNominee.getId());
						guardianRepository.save(entity.getGuardianEntity());
					}
				}
				savedEntity = populateNomineeAndGuardian(nomineeEntity.get(0).getApplicationId());
			} else {
				if (user.isEmpty()) {
					return commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
				} else {
					return commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
				}
			}
			if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(savedEntity);
				responseModel.setPage(EkycConstants.PAGE_DOCUMENT);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_NOMINEE_DETAILS);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.NOMINEE_AVAILABLE);
		}
		return responseModel;
	}

	/**
	 * Method to get Nominee Details
	 */
	@Override
	public ResponseModel getNominee(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		List<NomineeEntity> savedEntity = populateNomineeAndGuardian(applicationId);
		if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedEntity);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}

	/**
	 * Method to populate nominee and Guardian Details
	 * 
	 * @param applicationId
	 * @return
	 */
	public List<NomineeEntity> populateNomineeAndGuardian(long applicationId) {
		List<NomineeEntity> savedEntity = nomineeRepository.findByapplicationId(applicationId);
		if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
			savedEntity.forEach(entity -> {
				entity.setGuardianEntity(guardianRepository.findByNomineeId(entity.getId()));
			});
		}
		return savedEntity;
	}

}

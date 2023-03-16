package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.controller.spec.IPennyController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.service.spec.ISegmentService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class SegmentService implements ISegmentService {
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	IPennyController iPennyController;

	/**
	 * Method to save Segment Details
	 */
	@Override
	public ResponseModel saveSegment(SegmentEntity segmentEntity) {
		ResponseModel responseModel = new ResponseModel();
		SegmentEntity updatedEntity = null;
		Optional<ApplicationUserEntity> user = applicationUserRepository.findById(segmentEntity.getApplicationId());
		if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0) {
			SegmentEntity savedSegmentEntity = segmentRepository.findByapplicationId(segmentEntity.getApplicationId());
			if (savedSegmentEntity != null) {
				segmentEntity.setId(savedSegmentEntity.getId());
				updatedEntity = segmentRepository.save(segmentEntity);
			} else {
				updatedEntity = segmentRepository.save(segmentEntity);
			}
			if (updatedEntity != null && updatedEntity.getId() > 0) {
				commonMethods.UpdateStep(EkycConstants.PAGE_SEGMENT, segmentEntity.getApplicationId());
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(updatedEntity);
				responseModel.setPage(EkycConstants.PAGE_PAYMENT);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_SEGMENT_DETAILS);
			}
		} else {
			if (user.isEmpty()) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
			}
		}
		return responseModel;
	}

	/**
	 * Method to get Segment Details
	 */
	@Override
	public ResponseModel getSegmentByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		SegmentEntity savedSegmentEntity = segmentRepository.findByapplicationId(applicationId);
		if (savedSegmentEntity != null) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedSegmentEntity);
			responseModel.setPage(EkycConstants.PAGE_SEGMENT);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}
}

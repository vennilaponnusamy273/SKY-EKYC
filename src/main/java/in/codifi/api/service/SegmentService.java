package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.controller.spec.IPennyController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.helper.RejectionStatusHelper;
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
	@Inject
	RejectionStatusHelper rejectionStatusHelper;
	
	private static final Logger logger = LogManager.getLogger(SegmentService.class);
	/**
	 * Method to save Segment Details
	 */
	@Override
	public ResponseModel saveSegment(SegmentEntity segmentEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
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
			rejectionStatusHelper.insertArchiveTableRecord(segmentEntity.getApplicationId(),
					EkycConstants.PAGE_SEGMENT);
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
	} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(segmentEntity.getApplicationId(),"SegmentService","saveSegment",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In saveSegment for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to get Segment Details
	 */
	@Override
	public ResponseModel getSegmentByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		SegmentEntity savedSegmentEntity = segmentRepository.findByapplicationId(applicationId);
		if (savedSegmentEntity != null) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedSegmentEntity);
			responseModel.setPage(EkycConstants.PAGE_SEGMENT);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.SaveLog(applicationId,"SegmentService","getSegmentByAppId",e.getMessage());
		commonMethods.sendErrorMail("An error occurred while processing your request, In getSegmentByAppId for the Error: " + e.getMessage(),"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
	return responseModel;
}
}

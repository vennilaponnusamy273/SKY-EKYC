package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.ISegmentController;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.ISegmentService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/segment")
public class SegmentController implements ISegmentController {
	@Inject
	ISegmentService iSegmentService;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to save Segment Details
	 */
	@Override
	public ResponseModel saveSegment(SegmentEntity segmentEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (segmentEntity != null && segmentEntity.getApplicationId() > 0) {
			responseModel = iSegmentService.saveSegment(segmentEntity);
		} else {
			if (segmentEntity != null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
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
		if (applicationId > 0) {
			responseModel = iSegmentService.getSegmentByAppId(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	@Override
	public ResponseModel addBrokerage(long applicationId, String brokerageAcc) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			if (brokerageAcc.equalsIgnoreCase("sky prime") || brokerageAcc.equalsIgnoreCase("sky discount")) {
				responseModel = iSegmentService.addBrokerage(applicationId, brokerageAcc);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.INVAILD_BROKERAGE_TYPE);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

}

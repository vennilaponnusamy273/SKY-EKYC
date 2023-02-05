package in.codifi.api.service.spec;

import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.model.ResponseModel;

public interface ISegmentService {
	/**
	 * Method to save Segment Details
	 * 
	 * @author prade
	 * @param segmentEntity
	 * @return
	 */
	ResponseModel saveSegment(SegmentEntity segmentEntity);

	/**
	 * Method to get Segment Details
	 * 
	 * @author prade
	 * 
	 * @param applicationId
	 * @return
	 */
	ResponseModel getSegmentByAppId(long applicationId);
}

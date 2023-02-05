package in.codifi.api.service.spec;

import java.util.List;

import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.ResponseModel;

public interface INomineeService {

	/**
	 * Method to save Nominee Details
	 * 
	 * @author prade
	 * @param segmentEntity
	 * @return
	 */
	ResponseModel saveNominee(List<NomineeEntity> nomineeEntity);

	/**
	 * Method to get Nominee Details
	 * 
	 * @author prade
	 * 
	 * @param applicationId
	 * @return
	 */
	ResponseModel getNominee(long applicationId);
}
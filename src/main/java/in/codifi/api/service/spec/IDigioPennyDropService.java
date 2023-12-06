package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface IDigioPennyDropService {

	/**
	 * Method to Call pennyDrop via id
	 * 
	 * @author Vennila
	 * @param application id
	 * @return
	 */
	ResponseModel createPennyDrop(long applicationId);
}

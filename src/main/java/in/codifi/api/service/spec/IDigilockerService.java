package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface IDigilockerService {

	/**
	 * Method to intialize digi locker
	 * 
	 * @author prade
	 * @param PanCardDetails
	 * @return
	 */
	ResponseModel iniDigilocker(long applicationId);
}

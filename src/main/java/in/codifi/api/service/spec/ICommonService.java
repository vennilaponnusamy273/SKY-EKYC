package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface ICommonService {
	/**
	 * Method to get log details
	 * 
	 * @author gowthaman
	 * @return
	 */
	ResponseModel getLogDetails();

	ResponseModel pageJumb(String pagesnumber);

	/**
	 * Method to update nominee opted out
	 * 
	 * @param applicationId
	 * @return
	 */
	ResponseModel updateNomineeOptedOut(long applicationId);
}

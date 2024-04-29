package in.codifi.api.service.spec;

import in.codifi.api.model.PushDataModel;
import in.codifi.api.model.ResponseModel;

public interface ICamsDocService {

	
	/**
	 * Method to save cams Document
	 * 
	 * @author Vennila
	 * @param requestData
	 * @return
	 */
	ResponseModel getCamsPdf(PushDataModel  pushDataModel);

	

	/**
	 * Method to getCamsRedirectUrl
	 * 
	 * @author Vennila
	 * @param applicationId
	 * @return
	 */
	ResponseModel getCamsRedirectUrl(long applicationId);

}

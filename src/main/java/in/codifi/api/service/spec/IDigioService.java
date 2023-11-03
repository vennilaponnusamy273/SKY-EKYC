package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.WebhookDigioRequestModel;

public interface IDigioService {
	/**
	 * Method to intialize digio to open digi locker
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel iniDigio(long applicationId);

	/**
	 * Method to save address from digio
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel saveDigioAadhar(long applicationId);

	/**
	 * Method for web hook to check status for digi locker
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel whDigilocker(WebhookDigioRequestModel digioRequestModel);

}

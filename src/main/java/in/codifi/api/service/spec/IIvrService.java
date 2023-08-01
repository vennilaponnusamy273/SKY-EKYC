package in.codifi.api.service.spec;

import javax.validation.constraints.NotNull;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;

public interface IIvrService {

	/**
	 * Method to get IVR Details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel getIvr(long applicationId);

	/**
	 * Method to upload IVR Document
	 * 
	 * @author Vennila Ponnusamy
	 * @param FormData
	 * @return
	 */
	ResponseModel uploadIvr(IvrModel ivrModel);

	/**
	 * Method to generate IVR Link
	 * 
	 * @param ApplicationId
	 * @return
	 */
	ResponseModel getIvrLink(@NotNull long applicationId);

	/**
	 * Method to send link to mail or sms
	 */
	ResponseModel sendLink(ApplicationUserEntity userEntity, String type);

}

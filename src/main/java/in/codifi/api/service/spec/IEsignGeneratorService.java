package in.codifi.api.service.spec;

import javax.validation.constraints.NotNull;

import in.codifi.api.model.ResponseModel;

public interface IEsignGeneratorService {
	/**
	 * Method to get xml for E sign
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	ResponseModel xmlGenerator(@NotNull long applicationId);

	/**
	 * Method to get xml for E sign
	 * 
	 * @author prade
	 * @param xmlPath,getXml
	 * @return
	 */
	ResponseModel toGetTxnFromXMlpath(String xmlPath, String getXml);
}

package in.codifi.api.service.spec;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;

public interface IDocumentService {
	/**
	 * Method to upload file
	 * 
	 * @author Vennila Ponnusamy
	 * @param FormData
	 * @return
	 */

	ResponseModel uploadDoc(FormDataModel fileModel);

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
	ResponseModel getLinkIvr(@NotNull long applicationId);

	/**
	 * Method to check document present or not
	 * 
	 * @param ApplicationId
	 * @return
	 */
	ResponseModel getDocument(@NotNull long applicationId);

	/**
	 * Method to delete uploaded documents
	 * 
	 * @param applicationId
	 * @param Type
	 * @return
	 */
	ResponseModel deleteDocument(@NotNull long applicationId, @NotNull String type);

	Response downloadFile(@NotNull long applicationId, @NotNull String type);

}

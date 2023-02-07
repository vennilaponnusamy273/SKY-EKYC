package in.codifi.api.service.spec;

import in.codifi.api.model.FormDataModel;
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
}

package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IDocUpController;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IDocumentService;

@Path("/doc")
public class DocUpController implements IDocUpController {
	@Inject
	IDocumentService docservice;

	/**
	 * Method to Upload proof Documnt
	 */
	@Override
	public ResponseModel uploadDoc(FormDataModel fileModel) {
		ResponseModel response = new ResponseModel();
		response = docservice.uploadDoc(fileModel);
		return response;
	}

}

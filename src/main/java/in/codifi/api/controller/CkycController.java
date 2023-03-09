package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.ICkycController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.ICkycService;

@Path("/ckyc")
public class CkycController implements ICkycController {

	@Inject
	ICkycService service;

	/**
	 * Method to get details from ckyc api
	 */
	@Override
	public ResponseModel getckyc(long applicationID) {
		ResponseModel responsemodel = new ResponseModel();
		responsemodel = service.saveCkycResponse(applicationID);
		return responsemodel;
	}
}

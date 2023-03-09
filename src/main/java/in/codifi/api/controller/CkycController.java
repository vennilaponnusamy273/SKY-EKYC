package in.codifi.api.controller;

import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;

import in.codifi.api.controller.spec.ICkycController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.ICkycService;

@Path("/ckyc")
public class CkycController implements ICkycController {

	@Autowired
	ICkycService service;

	/**
	 * Method to get details from ckyc api
	 */
	@Override
	public ResponseModel getckyc(long applicationID) {
		ResponseModel responsemodel = new ResponseModel();
		responsemodel = service.getckyc(applicationID);
		return responsemodel;
	}
}

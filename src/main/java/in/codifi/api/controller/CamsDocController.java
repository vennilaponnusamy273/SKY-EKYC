package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.ICamsDocController;
import in.codifi.api.model.PushDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.ICamsDocService;

@Path("/cams")
public class CamsDocController  implements ICamsDocController{

	@Inject
	ICamsDocService iCamsDocService;
	@Override
	public ResponseModel getCamsPdf(PushDataModel  pushDataModel) {
		ResponseModel responseModel = new ResponseModel();
		System.out.println("the getCamsPdf Controller is running");
			responseModel = iCamsDocService.getCamsPdf(pushDataModel);
			return responseModel;
	}
	
	@Override
	public ResponseModel getCamsRedirectUrl(long applicationid) {
		ResponseModel responseModel = new ResponseModel();
			responseModel = iCamsDocService.getCamsRedirectUrl(applicationid);
			return responseModel;
	}

}

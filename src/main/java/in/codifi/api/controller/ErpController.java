package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IErpController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IErpService;
import in.codifi.api.utilities.CommonMethods;

@Path("/getERP")
public class ErpController implements IErpController {

	@Inject
	IErpService iErpService;
	
	@Inject
	CommonMethods commonMethods;
	
	@Override
	public ResponseModel userCreation(long mobileNo, String userId, String emailId, String password) {
		ResponseModel response=new ResponseModel();
		try {
			response=iErpService.getuser(mobileNo,userId,emailId,password);
		}
		catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
			return response;
		}

	@Override
	public ResponseModel DocumentUpload(String documentType, String userId, String base64Content) {
		ResponseModel response=new ResponseModel();
		try {
			response=iErpService.uploadDocument(documentType,userId,base64Content);
		}
		catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
			return response;
		}

}

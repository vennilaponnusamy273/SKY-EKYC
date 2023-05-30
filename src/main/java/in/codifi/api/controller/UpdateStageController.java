package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IUpdateStageController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IUpdateStageService;
import in.codifi.api.utilities.CommonMethods;

@Path("/update")
public class UpdateStageController implements IUpdateStageController {

	@Inject
	IUpdateStageService iUpdateStageService;
	
	@Inject
	CommonMethods commonMethods;
	
	
	@Override
	public ResponseModel UpdateStage(long applicationId, String stage,String status) {
		ResponseModel response=new ResponseModel();
		try {
			response=iUpdateStageService.updateUser(applicationId,stage,status);
		}
		catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
			return response;
		}
}

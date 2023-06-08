package in.codifi.api.controller;
import javax.inject.Inject;
import javax.ws.rs.Path;
import in.codifi.api.controller.spec.IErpController;
import in.codifi.api.service.spec.IErpService;
import in.codifi.api.utilities.CommonMethods;
import io.quarkus.scheduler.Scheduled;

@Path("/getERP")
public class ErpController implements IErpController {
	@Inject
	IErpService iErpService;
	
	@Inject
	CommonMethods commonMethods;
	
/**	@Override
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
		}**/
	/**@Override
	 public ResponseModel Schedular() {
        ResponseModel response = iErpService.getScheduledResponse();
        if (response != null) {
        	iErpService.resetScheduledResponse();
            return response;
        } else {
            response = commonMethods.constructFailedMsg("No data available");
            return response;
        }
    }**/

    @Scheduled(every = "5m")
    public void schedulerTask() {
        iErpService.processScheduler();
    }
}



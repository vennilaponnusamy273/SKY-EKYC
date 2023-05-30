package in.codifi.api.service;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.restservice.ErpRestService;
import in.codifi.api.service.spec.IErpService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
@ApplicationScoped
public class ErpService implements IErpService{

	@Inject
	ApplicationProperties props;
	
	@Inject
	ErpRestService erpRestService;
	
	@Inject
	ApplicationUserRepository repository;
	
	@Inject
	CommonMethods commonMethods;
	
		@Override
		public ResponseModel getuser(long mobileNo, String userId, String emailId, String password) {
		    ResponseModel response = new ResponseModel();
		    try {
		    	ApplicationUserEntity mobilenumberPresent=repository.findByMobileNo(mobileNo);
		    	ApplicationUserEntity emailPresent = repository.findByEmailId(emailId);
		    	if (mobilenumberPresent!=null &&emailPresent!=null&&mobilenumberPresent.getSmsVerified()>0&& emailPresent.getEmailVerified()>0) 
		    	{
		    	String responseMessage = erpRestService.UserCreation(mobileNo, userId, emailId, password);
		    	System.out.println(responseMessage);
		        response.setMessage(EkycConstants.SUCCESS_MSG);
		        response.setStat(EkycConstants.SUCCESS_STATUS);
		        response.setResult(responseMessage);
			    }
			    else
			    {
		    	response = commonMethods.constructFailedMsg(MessageConstants.INVAILD_EMAILID_MOBILEBO);
		    	}}
		    	catch (Exception e) {
		        response = commonMethods.constructFailedMsg(e.getMessage());
		        response.setMessage(EkycConstants.FAILED_MSG);
		        response.setStat(EkycConstants.FAILED_STATUS);
		        response.setResult(response);
		    }
		    return response;
		}
		@Override
		public ResponseModel uploadDocument(String documentType, String user_id, String base64Content) {
		    ResponseModel response = new ResponseModel();
		    try {
		    	String responseMessage=erpRestService.uploadDocument(documentType, user_id, base64Content);
		        response.setMessage(EkycConstants.SUCCESS_MSG);
		        response.setStat(EkycConstants.SUCCESS_STATUS);
		        response.setResult(responseMessage);
		    } catch (Exception e) {
		        response = commonMethods.constructFailedMsg(e.getMessage());
		        response.setMessage(EkycConstants.FAILED_MSG);
		        response.setStat(EkycConstants.FAILED_STATUS);
		    }
		    return response;
		}
}

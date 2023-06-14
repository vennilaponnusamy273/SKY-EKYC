package in.codifi.api.service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.controller.spec.IErpController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.UpdateErpEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.UpdateErpRepository;
import in.codifi.api.restservice.ErpRestService;
import in.codifi.api.service.spec.IErpService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import io.quarkus.scheduler.Scheduler;
@ApplicationScoped
public class ErpService implements IErpService{

	private AtomicReference<ResponseModel> scheduledResponse = new AtomicReference<>();
	
	@Inject
	ApplicationProperties props;
	
	@Inject
	ErpRestService erpRestService;
	
	@Inject
	ApplicationUserRepository repository;
	
	@Inject
    Scheduler scheduler;
	@Inject
	IErpController iErpController;
	
	@Inject
	DocumentRepository docrepository;
	
	@Inject
	CommonMethods commonMethods;
	@Inject
	UpdateErpRepository updateErpRepository;
	
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
		
		@Override
	    public ResponseModel getScheduledResponse() {
	        return scheduledResponse.get();
	    }

	    @Override
	    public void resetScheduledResponse() {
	        scheduledResponse.set(null);
	    }

	    @Override
	    public void processScheduler() {
	        resetScheduledResponse();
	        ResponseModel response = new ResponseModel();

	        try {
	            List<ApplicationUserEntity> recentlyCreatedUsers = repository.findRecentlyCreatedUsers();

	            for (ApplicationUserEntity user : recentlyCreatedUsers) {
	                if (updateErpRepository.findByMobileNo(user.getMobileNo()) == null) {
	                    String responseMessage = erpRestService.UserCreation(user.getMobileNo(), user.getId().toString(), user.getEmailId(), "");
	                    System.out.println("the responseMessage"+responseMessage);
	                    saveResponseErpuser(user.getMobileNo(), user.getId().toString(), user.getEmailId(), user.getPassword(), responseMessage);
	                }
	            }
	            List<DocumentEntity> recentlyCreatedUsersDoc = docrepository.findRecentlyDocUsers();

	            for (DocumentEntity entity : recentlyCreatedUsersDoc) {
	                String docTypeInErp = entity.getDocumentType().equalsIgnoreCase(EkycConstants.DOC_PAN) ? EkycConstants.DOC_PAN_ERP : entity.getDocumentType();
	                if(updateErpRepository.findByUserIdAndDoctype(entity.getApplicationId().toString(), docTypeInErp)==null) {
	                String path = entity.getAttachementUrl();
	                Path filePath = Paths.get(path);
	                byte[] fileBytes = Files.readAllBytes(filePath);
	                String base64String = Base64.getEncoder().encodeToString(fileBytes);
	                System.out.println("THE DocTypeInErp: " + docTypeInErp);
	                String responseMessage = erpRestService.uploadDocument(docTypeInErp, entity.getApplicationId().toString(), base64String);
	                saveResponseErpdoc(entity.getApplicationId().toString(), docTypeInErp, responseMessage);
	            }}
	            List<ApplicationUserEntity> recentlyCreatedUsersAll = repository.findRecentlyCreatedUsersAll();	            
	            for (ApplicationUserEntity userAll : recentlyCreatedUsersAll) {
	            	String AllMEssage=erpRestService.updatefulldetails(userAll.getId());
	            	System.out.println("the AllMEssage"+AllMEssage);
	            	saveResponseErpuserAll(userAll.getId().toString(),AllMEssage);
	            }
	        } catch (Exception e) {
	            response = commonMethods.constructFailedMsg(e.getMessage());
	            response.setMessage(EkycConstants.FAILED_MSG);
	            response.setStat(EkycConstants.FAILED_STATUS);
	            scheduledResponse.set(response);
	        }
	    }
	    public void saveResponseErpuser(long mobileNo, String userId, String emailId, String password, String erpResponse) {
	        UpdateErpEntity updateErpEntityUser = new UpdateErpEntity();
	        updateErpEntityUser.setEmailId(emailId);
	        updateErpEntityUser.setMobileNo(mobileNo);
	        updateErpEntityUser.setUserId(userId);
	        updateErpEntityUser.setPassword(password);
	        updateErpEntityUser.setErpResponse(erpResponse);
	        updateErpEntityUser.setErpApiType("UserCreation");
	        updateErpRepository.save(updateErpEntityUser);
	    }

	    public void saveResponseErpdoc(String userId, String docType, String responseMessage) {
	        UpdateErpEntity updateErpEntitydoc = new UpdateErpEntity();
	        updateErpEntitydoc.setUserId(userId);
	        updateErpEntitydoc.setDoctype(docType);
	        updateErpEntitydoc.setErpResponse(responseMessage);
	        updateErpEntitydoc.setErpApiType("DocumentUpload");
	        updateErpRepository.save(updateErpEntitydoc);
	    }
	    public void saveResponseErpuserAll(String userId,String erpResponse) {
	        UpdateErpEntity updateErpEntityUser = new UpdateErpEntity();
	        updateErpEntityUser.setUserId(userId);
	        updateErpEntityUser.setErpResponse(erpResponse);
	        updateErpEntityUser.setErpApiType("update full details");
	        updateErpRepository.save(updateErpEntityUser);
	    }
}

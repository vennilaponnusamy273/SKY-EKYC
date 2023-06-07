package in.codifi.api.service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
import in.codifi.api.utilities.StringUtil;
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
	    @Transactional
	    public void processScheduler() {
	    	 resetScheduledResponse();
	        ResponseModel response = new ResponseModel();
	        try {
	        	
	            Iterable<ApplicationUserEntity> users = repository.findAll();
	            Date currentTimestamp = new Date();
	            List<String> processedUsers = new ArrayList<>();
	            for (ApplicationUserEntity user : users) {
	            	UpdateErpEntity Exitdata=updateErpRepository.findByMobileNo(user.getMobileNo());
	            	if(Exitdata==null) {
	            	if(user.getSmsVerified()>0&&user.getEmailVerified()>0&&user.getPassword()!=null) {
	                long minutesDifference = (currentTimestamp.getTime() - user.getCreatedOn().getTime()) / (1000 * 60);
	                if (minutesDifference < 5) {
	                    processedUsers.add(user.getMobileNo()+user.getId()+user.getEmailId()+user.getPassword());
	                    String responseMessage = erpRestService.UserCreation(user.getMobileNo(), user.getId().toString(), user.getEmailId(), user.getPassword());
	                    saveResponseErpuser(user.getMobileNo(), user.getId().toString(), user.getEmailId(), user.getPassword(),responseMessage);
	                }
	                String processedUsersString = String.join(System.lineSeparator(), processedUsers);
		            response.setResult(processedUsersString);
		            scheduledResponse.set(response);
	            }
	            }}
	            
	            Iterable<DocumentEntity> documents = docrepository.findAll();
	            if(documents!=null) {
    			for (DocumentEntity entity:documents) {
    				String path = entity.getAttachementUrl();
    			    Path filePath = Paths.get(path);
    				byte[] fileBytes = Files.readAllBytes(filePath);
    		        String base64String = Base64.getEncoder().encodeToString(fileBytes);
    				String responseMessage=erpRestService.uploadDocument(entity.getDocumentType(), entity.getApplicationId().toString(), base64String);
    				saveResponseErpdoc(entity.getApplicationId().toString(),entity.getDocumentType(),responseMessage);
    			}}
                
	        } catch (Exception e) {
	            response = commonMethods.constructFailedMsg(e.getMessage());
	            response.setMessage(EkycConstants.FAILED_MSG);
	            response.setStat(EkycConstants.FAILED_STATUS);
	            scheduledResponse.set(response);
	        }
	    
	    }
	        
	    public void saveResponseErpuser(long Mobileno,String UserId,String EmailId,String password,String ErpResponse) {
	    	 UpdateErpEntity updateErpEntity=new UpdateErpEntity();
             updateErpEntity.setEmailId(EmailId);
             updateErpEntity.setMobileNo(Mobileno);
             updateErpEntity.setUserId(UserId);
             updateErpEntity.setPassword(password);
             updateErpEntity.setErpResponse(ErpResponse);
             updateErpRepository.save(updateErpEntity);
	    }
	    public void saveResponseErpdoc(String UserId,String docType,String responseMessage) {
	    	 UpdateErpEntity updateErpEntity=updateErpRepository.findByUserId(UserId);
	    	if(updateErpEntity!=null) {
	    	String ExitDoc=updateErpEntity.getDoctype()+","+docType;
            updateErpEntity.setDoctype(ExitDoc);
            updateErpEntity.setErpResponsedoc(responseMessage);
            updateErpRepository.save(updateErpEntity);
	    }}
	
}

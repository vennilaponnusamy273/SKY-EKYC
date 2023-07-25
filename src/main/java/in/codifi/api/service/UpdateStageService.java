package in.codifi.api.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.entity.UserStatus;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.UserStatusRepository;
import in.codifi.api.service.spec.IUpdateStageService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class UpdateStageService implements IUpdateStageService {

	@Inject
	UserStatusRepository userStatusRepository;

	@Inject
	CommonMethods commonMethods;
	
	@Override
	public ResponseModel updateUser(long applicationId, String stage, String status) {
	    ResponseModel response = new ResponseModel();
	    UserStatus newStatus = new UserStatus();
	    try {
	    	if(status.equalsIgnoreCase("online")||status.equalsIgnoreCase("offline")) {
	        UserStatus existingStatus = userStatusRepository.findByApplicationIdAndStageAndStatus(applicationId, stage, status);
	        if (existingStatus != null) {
	            existingStatus.setAttempt(existingStatus.getAttempt() + 1);
	            userStatusRepository.save(existingStatus);
	        } else {
	            newStatus.setApplicationId(applicationId);
	            newStatus.setStage(stage);
	            newStatus.setStatus(status);
	            newStatus.setAttempt(0);
	            userStatusRepository.save(newStatus);
	        }
	        response.setMessage(EkycConstants.SUCCESS_MSG);
	        response.setStat(EkycConstants.SUCCESS_STATUS);
	        response.setResult(existingStatus != null ? existingStatus : newStatus);
	    }else{
	    	response = commonMethods.constructFailedMsg(MessageConstants.UPDATESTAGE_STATUS_MSG);
	    }}
	    	catch (Exception e) {
	        response = commonMethods.constructFailedMsg(e.getMessage());
	        response.setMessage(EkycConstants.FAILED_MSG);
	        response.setStat(EkycConstants.FAILED_STATUS);
	        response.setResult(response);
	    }	
	    return response;
	}
//	@Scheduled(every = "2m30s")
	public void checkStatus() {
		/**LocalDateTime currentTimeMinusInterval = LocalDateTime.now().minusMinutes(2).minusSeconds(30);
	    Date formattedTime = Date.from(currentTimeMinusInterval.atZone(ZoneId.systemDefault()).toInstant());
	    List<UserStatus> userStatuses = userStatusRepository.findByOnlineUsers(formattedTime);

	    if (userStatuses != null) {
	        for (UserStatus userStatus : userStatuses) {
	            userStatus.setStatus("offline");
	            userStatusRepository.save(userStatus);
	        }
	    }**/
//	    System.out.println("The scheduler is running");
	}


}

package in.codifi.api.restservice;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.utilities.CommonMethods;
@ApplicationScoped
public class ErpRestService {

	@Inject
	@RestClient
	IerpRestService ierpRestService;
	@Inject
	ApplicationProperties props;

	@Inject 
	CommonMethods commonMethods;
	/**
	 * Method to get ERP details
	 * 
	 * @author Nila
	 * @param otp
	 * @param mobile Number
	 * @return
	 */
	
	 public String UserCreation(long mobileNo, String userId, String emailId, String password) {
	        String message = null;
	        try {
	            String authorizationHeader = "token "+props.getUserCreationauthToken();
	            String requestBody = String.format("{\"mobile_no\": \"%s\", \"user_id\": \"%s\",\"email_id\":\"%s\",\"password\":\"%s\"}", mobileNo, userId, emailId, password);
	            message = ierpRestService.createUser(authorizationHeader, requestBody);
	            System.out.println("Message: " + message);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return message;
	    }
	 public String uploadDocument(String documentType, String userId, String base64Content) {
		 String message = null;
		    try {
		    	String authorizationHeader = "token "+props.getUserCreationauthToken();
		        String requestData = "{\"user_id\":\"" + userId + "\",\"document_type\":\"" + documentType + "\"}";
		        message  = ierpRestService.uploadDocument(authorizationHeader, requestData, base64Content);
		        System.out.println("the message in doc"+message);
		    } catch (Exception e) {
		    	  e.printStackTrace();
		    }

		    return message;
		}
	 
}

package in.codifi.api.restservice;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.CamsEntity;
import in.codifi.api.model.CamsRedirectResModel;
import in.codifi.api.model.CamsResModel;
import in.codifi.api.repository.CamsRepository;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class CamsRestService {

	
	@Inject
	@RestClient
	ICamsRestService camsRestService;

	@Inject
	ApplicationProperties props;
	@Inject
	CamsRepository camsRepository;

	
	/**
	 * Method to findSessionIdAndToken
	 * 
	 * @return CamsResModel
	 */ 
	public CamsResModel findSessionIdAndToken() {
		CamsResModel camsResModel = null;
		try {
			String requestBodyString = "{\r\n    \"fiuID\":\""+props.getCamsFiuid()+"\",\r\n    \"redirection_key\":\""+props.getCamsreDirectId()+"\",\r\n    \"userId\":\""+props.getCamsUserId()+"\"\r\n}";
			camsResModel = camsRestService.findSessionIdAndToken(requestBodyString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return camsResModel;
	}


	public CamsRedirectResModel finDirectUrl(long applicationId, Long mobileNo, String token, String sessionId,String bankFid) {
	    CamsRedirectResModel camsRedirectResModel = null;
	    try {
	        // Generate a unique ID
	        String uniqueId = generateTransactionId();

	        // Prepare the request body
	        String requestBodyString =
	                "{\r\n" +
	                        "    \"clienttrnxid\":\""+uniqueId+"\",\r\n" +
	                        "    \"fiuID\":\"" + props.getCamsFiuid() + "\",\r\n" +
	                        "    \"userId\":\"" + props.getCamsUserId() + "\",\r\n" +
	                        "    \"aaCustomerHandleId\":\"" + mobileNo + "@CAMSAA\",\r\n" +
	                        "    \"aaCustomerMobile\":\"" + mobileNo + "\",\r\n" +
	                        "    \"sessionId\":\"" + sessionId + "\",\r\n" +
	                        "    \"useCaseid\":\"" + props.getCamsreuseCaseId() + "\",\r\n" +  // corrected the method name
//	                        "    \"fipid\":\""+bankFid+"\",\r\n" +
	                        "    \"addfip\":\"false\",\r\n" +
	                        "    \"redirect\":\""+EkycConstants.CAMS_REDIRECT_URL+"\"\r\n" +
	                        "}";

	        System.out.println("the requestBodyString"+requestBodyString);
	        // Prepare the authorization header
	        String headerToken = "Bearer " + token;

	        System.out.println("the requestBodyString finDirectUrl"+requestBodyString);
	        // Call the CamsRestService to get the redirection URL
	        camsRedirectResModel = camsRestService.finDirectUrl(headerToken, requestBodyString);

	        // Save the response data to the database
	        CamsEntity camsEntity = camsRepository.findByapplicationId(applicationId);
	        if (camsEntity == null) {
	            camsEntity = new CamsEntity();
	            camsEntity.setApplicationId(applicationId);
	        }
	        camsEntity.setClientTxtId(camsRedirectResModel.getClienttxnid());
	        camsEntity.setSessionid(camsRedirectResModel.getSessionId());
	        camsEntity.setToken(token);
	        camsEntity.setConsentHandleId(camsRedirectResModel.getConsentHandle());
	        camsEntity.setRedirectUrl(camsRedirectResModel.getRedirectionurl());
	        camsRepository.save(camsEntity);
	    } catch (Exception e) {
	        e.printStackTrace(); // Consider using a logger instead of printing the stack trace
	    }
	    return camsRedirectResModel;
	}

	
	public static String generateTransactionId() {
        String[] segments = new String[5];
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
        	if(i==0) {
        	segments[i] = String.format("%08x", random.nextInt(0x10000));
        	}else if(i==4) {
        		segments[i] = String.format("%012x", random.nextInt(0x10000));
        	}else {
        		segments[i] = String.format("%04x", random.nextInt(0x10000));
        	}
        }

        // Format the transaction ID
        return String.join("-", segments);
    }
}

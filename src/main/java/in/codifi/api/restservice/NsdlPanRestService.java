package in.codifi.api.restservice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
@ApplicationScoped
public class NsdlPanRestService {
	@Inject
	@RestClient
	INsdlPanRestService getPanNsdl;
	@Inject
	ApplicationProperties props;
	
	
//	public String GetNSdlDEtails(String data, String signature, String version) {
//		String NsdlRes = null;
//		try {
//			NsdlRes = getPanNsdl.getUser(data, signature, version);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return NsdlRes;
//	}
	
	public String GetNSdlDEtails(String inputString) {
	    String NsdlRes = null;
	    try {
	        LocalDateTime now = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	        String requestTime = now.format(formatter);
	        System.out.println("the requestTime"+requestTime);
	        String currentTimestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	        String transActionID = props.getPanPfxUserId() + ":" + currentTimestamp;
	        System.out.println("the transActionID"+transActionID);
	        // Call the method to get NSDL user
	        System.out.println("the inputString"+inputString);
	        NsdlRes = getPanNsdl.getUser(props.getPanPfxUserId(), "1", requestTime, transActionID, props.getPanVersion(), inputString);
	        System.out.println("the NsdlRes"+NsdlRes);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return NsdlRes;
	}
}

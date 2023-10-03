package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
@ApplicationScoped
public class NsdlPanRestService {
	@Inject
	@RestClient
	INsdlPanRestService getPanNsdl;
	
	
	public String GetNSdlDEtails(String data, String signature, String version) {
		String NsdlRes = null;
		try {
			NsdlRes = getPanNsdl.getUser(data, signature, version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NsdlRes;
	}
}

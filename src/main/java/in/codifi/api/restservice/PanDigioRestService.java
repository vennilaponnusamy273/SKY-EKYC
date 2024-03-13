package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.repository.ApplicationUserRepository;

@ApplicationScoped
public class PanDigioRestService {
	@Inject
	@RestClient
	IPanDigioRestService digioRestService;
	@Inject
	ApplicationProperties props;
	@Inject
	ApplicationUserRepository repository;
	

	/**
	 * Method to check liveness check
	 * 
	 * @param model
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public String getPanDetails(String panNo) {
		String apiModel = "";
		try {
			String requestBody = "{\"id_no\": \"" + panNo + "\"}";
			System.out.println("the requestBody" + requestBody);
			Response response = digioRestService.getPanDetails(props.getDigioAuthKey(), requestBody);
			if (response.getStatus() == 200) {
				apiModel = response.readEntity(String.class);
			} else {
				System.out.println("Non-200 response received. Status code: " + response.getStatus());
				apiModel = "Error: Non-200 response received. Status code: " + response.getStatus();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

}

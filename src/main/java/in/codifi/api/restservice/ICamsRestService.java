package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.springframework.web.bind.annotation.RequestBody;

import in.codifi.api.model.CamsRedirectResModel;
import in.codifi.api.model.CamsResModel;

@RegisterRestClient(configKey = "config-cams")
@RegisterClientHeaders
public interface ICamsRestService {

	
	/**
	 * Method to get auth token for aadhar
	 * 
	 * @param code
	 * @param grantType
	 * @param clientId
	 * @param clientSecret
	 * @param redirectUri
	 * @return
	 */
	@POST	
	@Path("/Authentication")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	CamsResModel findSessionIdAndToken(@RequestBody String requestBodyString);

	
	@POST
	@Path("/RedirectAA")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	CamsRedirectResModel finDirectUrl(@HeaderParam("authorization") String digioAuthKey,@RequestBody String requestBodyString);

}

package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-digi")
@RegisterClientHeaders
public interface IDigilockerRestService {
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
	@Path("/1/token")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	String getToken(@FormParam("code") String code, @FormParam("grant_type") String grantType,
			@FormParam("client_id") String clientId, @FormParam("client_secret") String clientSecret,
			@FormParam("redirect_uri") String redirectUri);

	/**
	 * Method to get eeadhar
	 * 
	 * @param authHeader
	 * @return
	 */
	@GET
	@Path("/3/xml/eaadhaar")
	@APIResponse(description = "")
	public String GetXmlAAthar(@HeaderParam("Authorization") String authHeader);
}

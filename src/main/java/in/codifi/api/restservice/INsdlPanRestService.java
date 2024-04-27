package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-pan")
@RegisterClientHeaders
public interface INsdlPanRestService {

//	@POST
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.TEXT_PLAIN)
//	public String getUser(@FormParam("data") String data, @FormParam("signature") String signature,
//			@FormParam("version") String version);
//	public String getUser();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getUser(@HeaderParam("User_ID") String User_ID,@HeaderParam("Records_count") String Records_count,
			@HeaderParam("Request_time") String Request_time,@HeaderParam("Transaction_ID") String Transaction_ID,@HeaderParam("Version") String Version,@RequestBody String inputdata);
}

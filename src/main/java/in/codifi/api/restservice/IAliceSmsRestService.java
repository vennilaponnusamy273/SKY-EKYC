package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-smsalice")
@RegisterClientHeaders
public interface IAliceSmsRestService {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendAliceSms(@QueryParam("apikey") String feedId, @QueryParam("senderid") String senderId,
			@QueryParam("number") String userName, @QueryParam("message") String password);
}

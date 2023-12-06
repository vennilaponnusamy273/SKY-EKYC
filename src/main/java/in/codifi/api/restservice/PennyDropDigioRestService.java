package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.api.model.PennyVerificationResModel;
@RegisterRestClient(configKey = "config-penny")
@RegisterClientHeaders
public interface PennyDropDigioRestService {

	@Path("/verify/bank_account")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	PennyVerificationResModel pennyUpdate(@HeaderParam("Authorization") String digioAuthKey, String ReqBody);
}

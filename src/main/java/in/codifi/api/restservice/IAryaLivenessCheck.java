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
import org.springframework.web.bind.annotation.RequestBody;

import in.codifi.api.model.LivenessCheckReqModel;
import in.codifi.api.model.LivenessCheckResModel;

@RegisterRestClient(configKey = "config-aryaai")
@RegisterClientHeaders
public interface IAryaLivenessCheck {

	@Path("/v1/liveness")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	public LivenessCheckResModel livenessCheck(@HeaderParam("token") String token,
			@RequestBody LivenessCheckReqModel model);

}

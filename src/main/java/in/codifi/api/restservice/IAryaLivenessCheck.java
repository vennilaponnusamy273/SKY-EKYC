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
import org.junit.jupiter.api.Timeout;
import org.springframework.web.bind.annotation.RequestBody;

import in.codifi.api.model.CkycRequestApiModel;
import in.codifi.api.model.LivenessCheckReqModel;
import in.codifi.api.model.LivenessCheckResModel;
import in.codifi.api.model.ckyc.CkycResponse;

@RegisterRestClient(configKey = "config-aryaai")
@RegisterClientHeaders
public interface IAryaLivenessCheck {
	/**
	 * Method to check liveness check
	 */
	@Path("/v1/liveness")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	public LivenessCheckResModel livenessCheck(@HeaderParam("token") String token,
			@RequestBody LivenessCheckReqModel model);

	/**
	 * Method to get CKYC data
	 */
	@Path("/v2/ckyc")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	@Timeout(50000)
	public CkycResponse getCKycData(@HeaderParam("token") String token,
			@RequestBody CkycRequestApiModel model);

}

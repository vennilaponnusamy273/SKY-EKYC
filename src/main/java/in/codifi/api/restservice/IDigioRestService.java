package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.api.model.DigioIniResponseModel;
import in.codifi.api.model.DigioRequestModel;
import in.codifi.api.model.DigioSaveAddResponse;

@RegisterRestClient(configKey = "config-digio")
@RegisterClientHeaders
public interface IDigioRestService {
	/**
	 * Method to check liveness check
	 */
	@Path("/request")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	DigioIniResponseModel digioInitialize(@HeaderParam("Authorization") String digioAuthKey, DigioRequestModel model);

	/**
	 * Method to check liveness check
	 */
	@Path("/{param}/response")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	DigioSaveAddResponse saveDigiAddress(@HeaderParam("Authorization") String digioAuthKey,
			@PathParam("param") String param);

	@Path("/media/{param}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	String getDigiXml(@HeaderParam("Authorization") String digioAuthKey,
			@PathParam("param") String param,@QueryParam("doc_type") String payload,@QueryParam("xml") String xml);
}

package in.codifi.api.restservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-kra")
@RegisterClientHeaders
public interface IKraPanRestService {
	@GET
	@Path("/SolicitPANDetailsFetchALLKRA")
	@Produces(MediaType.TEXT_PLAIN)
	@APIResponse(description = "")
	public String getKra(@QueryParam("InputXML") String inputXML, @QueryParam("username") String username,
			@QueryParam("PosCode") String posCode, @QueryParam("password") String password,
			@QueryParam("PassKey") String passKey);

	@GET
	@Path("/GetPanStatus")
	@Produces(MediaType.TEXT_PLAIN)
	@APIResponse(description = "")
	public String getPanStatus(@QueryParam("panNo") String panNo, @QueryParam("username") String username,
			@QueryParam("PosCode") String posCode, @QueryParam("password") String password,
			@QueryParam("PassKey") String passKey);
}

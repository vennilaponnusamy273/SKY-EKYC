package in.codifi.api.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.ResponseModel;

public interface IDigilockerController {
	/**
	 * Method to intialize digilocker
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/iniDigilocker")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to intialize Digilocker")
	public ResponseModel iniDigilocker(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to save address
	 * 
	 * @author Vennila Ponnusamy
	 * @param code
	 * @param state
	 * @return
	 */
	@Path("/saveDigi")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save Address from Digilocker")
	public ResponseModel saveDigi(@NotNull @QueryParam("code") String code, @NotNull @QueryParam("state") String state,
			@NotNull @QueryParam("applicationId") long applicationId);
}

package in.codifi.api.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.ResponseModel;

public interface IDigioPennyDropController {

	/**
	 * Method to create  penny Amount
	 * 
	 * @author Vennila
	 * @param application id
	 * @return
	 */
	@Path("/createDigioPenny")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method add put penny amount")
	ResponseModel createPenny(@NotNull @QueryParam("applicationId") long applicationId);

}

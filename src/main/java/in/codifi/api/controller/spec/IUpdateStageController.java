package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.ResponseModel;

public interface IUpdateStageController {

	/**
	 * Method to IUpdateStage 
	 * 
	 * @author VENNILA
	 * @param 
	 * @return
	 */
	
	@Path("/UpdateStage")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get USer Stage")
	ResponseModel UpdateStage(@NotNull @QueryParam("applicationId") long applicationId,@NotNull @QueryParam("Stage") String stage,@NotNull @QueryParam("status") String status);
}

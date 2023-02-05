package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.model.ResponseModel;

public interface IProfileController {
	/**
	 * Method to save Profile Details
	 * 
	 * @author prade
	 * @param profileEntity
	 * @return
	 */
	@Path("/saveProfile")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save Profile Details")
	ResponseModel saveProfile(ProfileEntity profileEntity);

	/**
	 * Method to get Profile Details
	 * 
	 * @author prade
	 * @param profileEntity
	 * @return
	 */
	@Path("/getProfile")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get Profile Details")
	ResponseModel getProfileByAppId(@NotNull @QueryParam("applicationId") long applicationId);
}

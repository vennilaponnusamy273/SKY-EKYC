package in.codifi.api.controller.spec;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.ResponseModel;

public interface INomineeController {
	/**
	 * Method to save nominee and Guardian Details
	 * 
	 * @author prade
	 * @param segmentEntity
	 * @return
	 */
	@Path("/saveNominee")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save nominee Details")
	ResponseModel saveNominee(List<NomineeEntity> nomineeEntity);

	/**
	 * Method to get nominee and Guardian Details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/getNominee")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get nominee Details")
	ResponseModel getNominee(@NotNull @QueryParam("applicationId") long applicationId);
}

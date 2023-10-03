package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;

public interface IPanController {
	/**
	 * Method to save pan id to get details
	 * 
	 * @author Vennila Ponnusamy
	 * @param pan
	 * @return
	 */
	@Path("/getPan")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to pan id to get  details")
	public ResponseModel getPanDetails(@RequestBody ApplicationUserEntity userEntity);

	/**
	 * Method to Confirm Pan Details
	 * 
	 * @author prade
	 * @param pan
	 * @return
	 */
	@Path("/confirmPan")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to Confirm Pan Details")
	public ResponseModel confirmPan(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to save DOB
	 * 
	 * @author prade
	 * @param pan
	 * @return
	 */
	@Path("/saveDOB")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save DOB")
	public ResponseModel saveDob(@RequestBody ApplicationUserEntity userEntity);

	/**
	 * Method to Confirm KRA Address
	 * 
	 * @author prade
	 * @param pan
	 * @return
	 */
	@Path("/confirmAddress")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to Confirm Address")
	public ResponseModel confirmAddress(@NotNull @QueryParam("applicationId") long applicationId);

}

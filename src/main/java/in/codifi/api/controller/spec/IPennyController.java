package in.codifi.api.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.ResponseModel;

public interface IPennyController {
	/**
	 * Method to Create Contact for penny drop
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	@Path("/createContact")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method Create Contact")
	ResponseModel createContact(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to add Account in created Contact
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	@Path("/addAccount")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method add account in created contact")
	ResponseModel addAccount(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to put some penny Amount
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	@Path("/createPayout")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method add put penny amount")
	ResponseModel createPayout(@NotNull @QueryParam("applicationId") long applicationId,
			@NotNull @QueryParam("confirmPenny") int confirmPenny);

	/**
	 * Method to Validate Penny Details
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	@Path("/ValidateDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method add validate Penny Details")
	ResponseModel ValidateDetails(@NotNull @QueryParam("applicationId") long applicationId);

}

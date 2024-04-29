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

import in.codifi.api.model.PushDataModel;
import in.codifi.api.model.ResponseModel;

public interface ICamsDocController {

	
	/**
	 * Method to save cams Document
	 * 
	 * @author Vennila
	 * @param requestData
	 * @return
	 */
	@Path("/getCamsPdf")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to retrieve CAMS PDF")
	ResponseModel getCamsPdf(PushDataModel  pushDataModel);

	
	/**
	 * Method to save cams Document
	 * 
	 * @author Vennila
	 * @param requestData
	 * @return
	 */
	@Path("/getCamsRedirectUrl")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to retrieve CAMS PDF")
	ResponseModel getCamsRedirectUrl(@NotNull @QueryParam("applicationId") long applicationId);
}

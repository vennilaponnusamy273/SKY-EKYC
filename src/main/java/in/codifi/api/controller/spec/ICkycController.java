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

public interface ICkycController {


	/**
	 * Method to get CYKC  details 
	 * 
	 * @author  prade
	 * @param CkycEntity
	 * @return
	 */
	
	@Path("/getCkyc")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get ckyc details")
	public ResponseModel getckyc(@NotNull @QueryParam("applicationId") long applicationId);
	
	
}


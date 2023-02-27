package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.springframework.web.bind.annotation.RequestBody;

import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.model.ResponseModel;

public interface ICkycController {


	/**
	 * Method to get CYKC  details 
	 * 
	 * @author  prade
	 * @param CkycEntity
	 * @return
	 */
	
	@Path("/ckyc_")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get ckyc details")
	public ResponseModel getckyc(@RequestBody ResponseCkyc ckyc);
	
	
}


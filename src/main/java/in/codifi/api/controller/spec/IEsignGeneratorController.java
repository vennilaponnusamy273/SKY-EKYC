package in.codifi.api.controller.spec;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import in.codifi.api.model.ResponseModel;

public interface IEsignGeneratorController {
	/**
	 * Method to get xml for E sign
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/xmlGenerator")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to generator xml for E sign")
	public ResponseModel xmlGenerator(@NotNull @QueryParam("applicationId") long applicationId);
	
	
	
	/**
	 * Method to get xml path
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/Getxml")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to generator xml for E sign")
	public ResponseModel ToGetTxnFromXMlpath(@NotNull @QueryParam("xmlPath") String xmlPath,@NotNull @QueryParam("XmlCode") String XmlCode);
	
}

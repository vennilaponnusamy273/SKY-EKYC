package in.codifi.api.controller.spec;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;

public interface IIvrController {

	/**
	 * Method to get IVR Details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/getIvr")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get nominee Details")
	ResponseModel getIvr(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to Upload Ivr Proof Details
	 * 
	 * @author Vennila Ponnusamy
	 * @param documentEntity
	 * @return
	 */
	@Path("/uploadIvr")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to Upload ivr Documnt")
	public ResponseModel uploadIvr(IvrModel ivrModel);

	/**
	 * Method to generate IVR Link
	 * 
	 * @param ApplicationId
	 * @return
	 */
	@Path("/getIvrLink")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get ivr link")
	public ResponseModel getIvrLink(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to send link to mail or sms
	 * 
	 * @param ApplicationId
	 * @return
	 */
	@Path("/sendLink")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get Documents that need to upload")
	ResponseModel sendLink(@NotNull @QueryParam("applicationId") long applicationId,
			@NotNull @QueryParam("type") String type);
}

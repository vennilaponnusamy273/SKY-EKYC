package in.codifi.api.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.WebhookDigioRequestModel;

public interface IDigioController {
	/**
	 * Method to intialize digio to open digi locker
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/iniDigio")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to intialize Digio")
	public ResponseModel iniDigio(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to save address from digio
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/saveDigio")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to intialize Digio")
	public ResponseModel saveDigioAadhar(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method for web hook to check status for digi locker
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/whDigilocker")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to intialize Digio")
	public ResponseModel whDigilocker(@NotNull WebhookDigioRequestModel digioRequestModel);
}

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

public interface ICommonController {

	/**
	 * 
	 * @return
	 */
	@GET
	@Path("/reloadKraKeyValue")
	@Produces(MediaType.TEXT_PLAIN)
	public String reloadKraKeyValue();

	/**
	 * Method to get address by pincode
	 * 
	 * @author prade
	 * @param ifsc
	 * @return
	 */
	@Path("/getAddress")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get address via pincode")
	ResponseModel getAddress(@NotNull @QueryParam("address") String address,@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to get log details
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Path("/getLogDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel getLogDetails(@NotNull @QueryParam("applicationId") long applicationId);
	
	/**
	 * Method to update Nominee OptedOut
	 * 
	 * @author pradeep
	 * @return
	 */
	@Path("/nomineeOptedOut")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel updateNomineeOptedOut(@NotNull @QueryParam("applicationId") long applicationId);
	
	/**
	 * Method to provide pageJumb
	 * 
	 * @author vennila
	 * @param pages 
	 * @return
	 */
	@Path("/pageJumb")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to set page as REjection")
	ResponseModel pageJumb(@NotNull @QueryParam("pagesnumber")String pagesnumber,@NotNull @QueryParam("applicationId") long applicationId) ;
}

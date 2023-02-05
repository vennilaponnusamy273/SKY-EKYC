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

import in.codifi.api.entity.BankEntity;
import in.codifi.api.model.ResponseModel;

public interface IBankController {
	/**
	 * Method to save Bank Details
	 * 
	 * @author prade
	 * @param bankEntity
	 * @return
	 */
	@Path("/saveBank")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save Profile Details")
	ResponseModel saveBank(BankEntity bankEntity);

	/**
	 * Method to get Bank Details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/getBank")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get Profile Details")
	ResponseModel getBankByAppId(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to get Bank address by IFSC
	 * 
	 * @author prade
	 * @param ifsc
	 * @return
	 */
	@Path("/getBankAdd")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get Profile Details")
	ResponseModel getBankAdd(@NotNull @QueryParam("ifsc") String ifsc);
}

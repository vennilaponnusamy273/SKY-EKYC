package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.MultipartForm;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.NomineeDocModel;
import in.codifi.api.model.ResponseModel;

@SuppressWarnings("removal")
public interface INomineeController {
	/**
	 * Method to get nominee and Guardian Details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/getNominee")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get nominee Details")
	ResponseModel getNominee(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to save and upload Nominee Proof
	 * 
	 * @author prade
	 * @param fileModel
	 * @return
	 */

	@Path("/uploadNominee")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@APIResponse(description = "Method to upload nominee file")
	ResponseModel uploadNominee(@MultipartForm NomineeDocModel fileModel);

	/**
	 * Method to update Nominee Alloction
	 * 
	 * @author prade
	 * @param paymentEntity
	 * @return
	 */
	@Path("/updateNomAlloc")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to update Nominee allocation")
	ResponseModel updateNomineeAllocation(NomineeEntity nomineeEntity);

	/**
	 * Method to delete Nominee
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/deleteNom")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to delete nominee Details")
	ResponseModel deleteNom(@NotNull @QueryParam("id") long id,@NotNull @QueryParam("applicationId") long applicationId);
}

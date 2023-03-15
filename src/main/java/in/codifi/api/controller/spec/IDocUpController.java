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
import org.jboss.resteasy.reactive.MultipartForm;

import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.ResponseModel;

@SuppressWarnings("removal")
public interface IDocUpController {
	/**
	 * Method to Upload proof Documnt
	 * 
	 * @author Vennila Ponnusamy
	 * @param documentEntity
	 * @return
	 */
	@Path("/upload")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@APIResponse(description = "Method to Upload proof Documnt")
	public ResponseModel uploadDoc(@MultipartForm FormDataModel fileModel);

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
	@APIResponse(description = "Method to Upload proof Documnt")
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
	 * Method to check document present or not
	 * 
	 * @param ApplicationId
	 * @return
	 */
	@Path("/getDocument")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to check document ")
	public ResponseModel getDocument(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to delete uploaded documents
	 * 
	 * @param applicationId
	 * @param Type
	 * @return
	 */
	@Path("/deleteDocument")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to Delete document ")
	public ResponseModel deleteDocument(@NotNull @QueryParam("applicationId") long applicationId,
			@NotNull @QueryParam("documentType") String type);

}

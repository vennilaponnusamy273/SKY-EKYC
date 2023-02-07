package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.MultipartForm;

import in.codifi.api.model.FormDataModel;
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
}

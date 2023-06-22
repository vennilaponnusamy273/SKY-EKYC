package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;
import io.smallrye.common.constraint.NotNull;

public interface IPdfController {


	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Path("/savePdf")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save PDF")
	public Response savePdf(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to save PDF data coordinates
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Path("/generateEsign")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save PDF")
	ResponseModel generateEsign(PdfApplicationDataModel pdfModel);
	
	/**
	 * Method to re direct from NSDL
	 * 
	 * @author prade
	 */
	@Path("/getNsdlXml")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@APIResponse(description = "Test")
	Response getNsdlXml(@FormParam("msg") String msg);

}

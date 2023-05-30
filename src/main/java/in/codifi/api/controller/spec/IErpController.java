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
public interface IErpController {

	/**
	 * Method to GetUserDetails 
	 * 
	 * @author VENNILA
	 * @param 
	 * @return
	 */
	
	@Path("/UserCreation")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get USer records")
	ResponseModel userCreation(@NotNull @QueryParam("mobile_no") long mobile_no,@NotNull @QueryParam("user_id") String user_id,@NotNull @QueryParam("EmailId") String EmailId,@NotNull @QueryParam("password") String password);
	
	@Path("/DocumentUpload")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@APIResponse(description = "Method to get USer records")
	ResponseModel DocumentUpload(@NotNull @QueryParam("documentType")String documentType, @NotNull @QueryParam("userId")String userId, @NotNull @QueryParam("base64Content") String base64Content);
}

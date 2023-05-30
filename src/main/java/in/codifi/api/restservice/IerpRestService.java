package in.codifi.api.restservice;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
//@RegisterRestClient(baseUri = "https://erp.nidhihq.com/api/method/")
@RegisterRestClient(configKey="config-erpbase")
public interface IerpRestService {

    @GET
    @Path("sky_broking.ekyc_apis.opportunity.user_creation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String createUser(@HeaderParam("Authorization") String authorizationHeader,@QueryParam("data") String requestData);
    
    @POST
    @Path("sky_broking.ekyc_apis.opportunity.pan_card_doc_upload")
    @Produces(MediaType.APPLICATION_JSON)
    String uploadDocument(
        @HeaderParam("Authorization") String authorizationHeader,
        @QueryParam("data") String requestData,
        @FormParam("base64content") String base64Content
    );

}
package in.codifi.api.restservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-razorpay")
@RegisterClientHeaders
public interface IRazorpayIfscRestService {
	/**
	 * method to get bank details by ifsc
	 * 
	 * @author Nila
	 * @param ifsc
	 * @return
	 */
	@GET
	@Path("{ifsc}")
	@Produces(MediaType.APPLICATION_JSON)
	String getBankAddressByIfsc(@PathParam("ifsc") String ifsc);
}

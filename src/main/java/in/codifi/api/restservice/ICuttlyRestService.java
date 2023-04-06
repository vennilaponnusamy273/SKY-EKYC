package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-bitlybase")
@RegisterClientHeaders
public interface ICuttlyRestService {
	/**
	 * method to shorten the ivr URl
	 * 
	 * @author Nila
	 * @param ifsc
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	String generateShortLink(@QueryParam("key") String apiKey, @QueryParam("short") String apiUrl) throws Exception;
}

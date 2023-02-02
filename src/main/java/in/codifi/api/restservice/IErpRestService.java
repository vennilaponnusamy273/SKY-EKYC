package in.codifi.api.restservice;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.api.model.ErpExistingApiModel;

@RegisterRestClient(configKey = "config-erp")
@RegisterClientHeaders
public interface IErpRestService {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ErpExistingApiModel checkExistingClient(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@QueryParam("data") String data);

}

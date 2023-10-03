package in.codifi.api.restservice.keycloak;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.api.model.CreateUserRequestModel;
import in.codifi.api.model.GetKeyCloakUser;
import in.codifi.api.model.GetUserInfoResp;

@RegisterRestClient(configKey = "auth-user-api")
@RegisterClientHeaders
public interface IKeyCloakAdminRestService {
	/**
	 * 
	 * @param authHeader
	 * @param user
	 */
	@Path("/users")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Create a new user after phone, email and mpin registration")
	public void addNewUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader, CreateUserRequestModel user);

	/**
	 * 
	 * @param authHeader
	 * @param user
	 */
	@Path("/users")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Create a new user after phone, email and mpin registration")
	public List<GetKeyCloakUser> getUserDetails(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@QueryParam("email") String email, @QueryParam("username") String username);

	@Path("/users/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To get user info to check whether user exist or not")
	public List<GetUserInfoResp> getUserInfo(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@QueryParam(value = "username") String username, @QueryParam(value = "exact") String exact);

	@Path("/users/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "To get user info by attribute to check whether user exist or not")
	public List<GetUserInfoResp> getUserInfoByAttribute(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
			@QueryParam(value = "q") String request, @QueryParam(value = "exact") String exact);
}

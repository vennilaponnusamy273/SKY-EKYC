package in.codifi.api.restservice.keycloak;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.KeyCloakConfig;
import in.codifi.api.model.CreateUserRequestModel;
import in.codifi.api.model.GetKeyCloakUser;
import in.codifi.api.model.GetUserInfoResp;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class KeyCloakAdminRestService {

	@Inject
	@RestClient
	IKeyCloakAdminRestService iKeyCloakAdminRestService;
	@Inject
	KeyCloakConfig props;
	@Inject
	KeyCloakTokenRestService cloakTokenRestService;

	/**
	 * User Creation in Keycloak
	 * 
	 * @param user
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public String addNewUser(CreateUserRequestModel user) throws ClientWebApplicationException {
		String message = "User Created";
		int count = 1;
		ObjectMapper mapper = new ObjectMapper();
		try {
			try {
				System.out.println(mapper.writeValueAsString(user));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String token = "Bearer " + getAccessToken();
			iKeyCloakAdminRestService.addNewUser(token, user);
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401 && count > 0) {
				count--;
				HazleCacheController.getInstance().getKeycloakAdminSession().clear();
				addNewUser(user);
			} else if (statusCode == 409) {
				return "User already exists";
			} else if (count == 0) {
				message = "";
			} else {
				e.printStackTrace();
				message = e.getMessage();
			}
		}
		return message;
	}

	private String getAccessToken() {
		if (HazleCacheController.getInstance().getKeycloakAdminSession().containsKey(props.getAdminClientId())) {
			return HazleCacheController.getInstance().getKeycloakAdminSession().get(props.getAdminClientId());
		}
		return cloakTokenRestService.getAdminAccessToken();
	}

	/**
	 * Check user available in keycloak
	 * 
	 * @param user
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public boolean checkUser(String mail, String mobileNumber) throws ClientWebApplicationException {
		boolean userPresent = false;
		try {
			String token = "Bearer " + getAccessToken();
			List<GetKeyCloakUser> cloakUser = iKeyCloakAdminRestService.getUserDetails(token, mail, mobileNumber);
			if (StringUtil.isListNotNullOrEmpty(cloakUser)
					&& StringUtil.isNotNullOrEmpty(cloakUser.get(0).getEmail())) {
				userPresent = true;
			}
		} catch (ClientWebApplicationException e) {
			e.printStackTrace();
		}
		return userPresent;
	}

	/**
	 * 
	 * Method to get user by userName to check whether user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userName
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public List<GetUserInfoResp> getUserInfo(String userName) throws ClientWebApplicationException {
		List<GetUserInfoResp> response = new ArrayList<>();
		try {

			String token = "Bearer " + getAccessToken();
			response = iKeyCloakAdminRestService.getUserInfo(token, userName.toLowerCase(), EkycConstants.TRUE);
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazleCacheController.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 
	 * Method to get user by attribute to check whether user exist or not
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param key
	 * @param value
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public List<GetUserInfoResp> getUserInfoByAttribute(String key, String value) throws ClientWebApplicationException {
		List<GetUserInfoResp> response = new ArrayList<>();
		try {
			String token = "Bearer " + getAccessToken();
			String request = key + ":" + value;
			response = iKeyCloakAdminRestService.getUserInfoByAttribute(token, request, EkycConstants.TRUE);
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazleCacheController.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
		}
		return response;
	}
}

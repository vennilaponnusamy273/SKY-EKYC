package in.codifi.api.restservice.keycloak;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.KeyCloakConfig;
import in.codifi.api.model.CreateUserRequestModel;

@ApplicationScoped
public class KeyCloakAdminRestService {

	@Inject
	@RestClient
	IKeyCloakAdminRestService iKeyCloakAdminRestService;
	@Inject
	KeyCloakConfig props;
	@Inject
	KeyCloakTokenRestService cloakTokenRestService;

	public String addNewUser(CreateUserRequestModel user) throws ClientWebApplicationException {
		String message = "User Created";
		int count = 1;
		try {
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
}

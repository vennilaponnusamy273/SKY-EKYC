package in.codifi.api.restservice.keycloak;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.KeyCloakConfig;
import in.codifi.api.model.CreateUserRequestModel;

public class KeyCloakAdminRestService {

	@Inject
	@RestClient
	IKeyCloakAdminRestService iKeyCloakAdminRestService;
	@Inject
	KeyCloakConfig props;
	@Inject
	KeyCloakTokenRestService cloakTokenRestService;

	public void addNewUser(CreateUserRequestModel user) throws ClientWebApplicationException {
		try {
			String token = "Bearer " + getAccessToken();
			iKeyCloakAdminRestService.addNewUser(token, user);
		} catch (ClientWebApplicationException e) {
			int statusCode = e.getResponse().getStatus();
			if (statusCode == 401)
				HazleCacheController.getInstance().getKeycloakAdminSession().clear();
			e.printStackTrace();
			e.printStackTrace();
		}
	}

	private String getAccessToken() {
		if (HazleCacheController.getInstance().getKeycloakAdminSession().containsKey(props.getAdminClientId())) {
			return HazleCacheController.getInstance().getKeycloakAdminSession().get(props.getAdminClientId());
		}
		return cloakTokenRestService.getAdminAccessToken();
	}
}

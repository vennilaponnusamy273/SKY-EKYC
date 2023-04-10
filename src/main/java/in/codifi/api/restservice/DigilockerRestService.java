package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;

@ApplicationScoped
public class DigilockerRestService {
	@Inject
	@RestClient
	IDigilockerRestService IdigiService;
	@Inject
	ApplicationProperties props;

	/**
	 * Method to get auth token for aadhar
	 * 
	 * @param code
	 * @param grantType
	 * @param clientId
	 * @param clientSecret
	 * @param redirectUri
	 * @return
	 */
	public String getAccessToken(String code) {
		String Message = null;
		try {
			String grandType = "authorization_code";
			Message = IdigiService.getToken(code, grandType, props.getDigiClientId(), props.getDigiSecret(),
					props.getDigiRedirectUrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Message;
	}

	/**
	 * Method to get eeadhar
	 * 
	 * @param authHeader
	 * @return
	 */
	public String getXml(String accessToken) {
		String Message = null;
		try {
			String authorizationHeader = "Bearer " + accessToken;
			Message = IdigiService.GetXmlAAthar(authorizationHeader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Message;
	}
}

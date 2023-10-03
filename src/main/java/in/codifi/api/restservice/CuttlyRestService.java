package in.codifi.api.restservice;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.JSONObject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class CuttlyRestService {
	@Inject
	@RestClient
	ICuttlyRestService bitlyService;

	@Inject
	ApplicationProperties props;

	/**
	 * Method to shorten the url
	 * 
	 * @param longUrl
	 * @return
	 */
	public String shortenUrl(String longUrl) {
		try {
			String shortUrl = bitlyService.generateShortLink(props.getBitlyAccessToken(),
					URLEncoder.encode(longUrl, StandardCharsets.UTF_8));
			JSONObject responseJson = new JSONObject(shortUrl);
			shortUrl = responseJson.getJSONObject(EkycConstants.URL).getString(EkycConstants.SHORT_URL);
			return shortUrl;
		} catch (Exception e) {
			return null;
		}
	}
}

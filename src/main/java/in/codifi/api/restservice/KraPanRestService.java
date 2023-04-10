package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;

@ApplicationScoped
public class KraPanRestService {
	@Inject
	@RestClient
	IKraPanRestService kraService;

	@Inject
	ApplicationProperties props;

	public String getPanKra(String xmcode) {
		String message = null;
		try {
			String passKey = "";
			message = kraService.getKra(xmcode, props.getKraUsername(), props.getKraPosCode(), props.getKraPassword(),
					passKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	public String getpanStatus(String panCard) {
		String message = null;
		try {
			String passKey = "";
			message = kraService.getPanStatus(panCard, props.getKraUsername(), props.getKraPosCode(),
					props.getKraPassword(), passKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
}

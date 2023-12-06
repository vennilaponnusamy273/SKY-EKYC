package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.model.PennyVerificationResModel;

@ApplicationScoped
public class PennyDropDigioService {

	@Inject
	@RestClient
	PennyDropDigioRestService pennyDropDigioRestService;
	@Inject	
	ApplicationProperties props;
	public PennyVerificationResModel pennyUpdate(String ReqBody) throws ClientWebApplicationException {
		PennyVerificationResModel apiModel = new PennyVerificationResModel();
		try {
			apiModel= pennyDropDigioRestService.pennyUpdate(props.getDigioAuthKey(), ReqBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}
}

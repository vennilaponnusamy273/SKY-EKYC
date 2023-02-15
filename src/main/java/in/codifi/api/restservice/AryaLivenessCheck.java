package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.model.LivenessCheckReqModel;
import in.codifi.api.model.LivenessCheckResModel;

@ApplicationScoped
public class AryaLivenessCheck {
	@Inject
	@RestClient
	IAryaLivenessCheck aryaLivenessCheck;
	@Inject
	ApplicationProperties props;

	public LivenessCheckResModel livenessCheck(LivenessCheckReqModel model) throws ClientWebApplicationException {
		LivenessCheckResModel apiModel = null;
		try {
			apiModel = aryaLivenessCheck.livenessCheck(props.getAryaAiToken(), model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}
}

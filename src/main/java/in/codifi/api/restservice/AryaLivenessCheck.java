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

	/**
	 * Method to check liveness check
	 * 
	 * @param model
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public LivenessCheckResModel livenessCheck(LivenessCheckReqModel model) throws ClientWebApplicationException {
		LivenessCheckResModel apiModel = null;
		try {
			apiModel = aryaLivenessCheck.livenessCheck(props.getAryaAiToken(), model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

	/**
	 * Method to get CKYC data
	 * 
	 * @param model
	 * @return
	 * @throws ClientWebApplicationException
	 */
//	public CkycResponse getCKycData(CkycRequestApiModel model) throws ClientWebApplicationException {
//		CkycResponse apiModel = null;
//		try {
//			apiModel = aryaLivenessCheck.getCKycData(props.getCkycAiToken(), model);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return apiModel;
//	}
}

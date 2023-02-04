package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.model.ErpExistingApiModel;
import in.codifi.api.model.ExistingCustReqModel;

@ApplicationScoped
public class ErpRestService {
	@Inject
	@RestClient
	IErpRestService erpRestService;
	@Inject
	ApplicationProperties props;

	public ErpExistingApiModel erpCheckExisting(ExistingCustReqModel model) throws ClientWebApplicationException {
		String param = "";
		ErpExistingApiModel apiModel = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			param = mapper.writeValueAsString(model);
			apiModel = erpRestService.checkExistingClient(props.getErpToken(), param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}
}

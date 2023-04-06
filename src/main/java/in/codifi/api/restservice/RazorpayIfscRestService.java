package in.codifi.api.restservice;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.model.BankAddressModel;

@ApplicationScoped
public class RazorpayIfscRestService {
	@Inject
	@RestClient
	IRazorpayIfscRestService commonRestService;
	@Inject
	ApplicationProperties props;

	/**
	 * Method to find bank address by ifsc
	 * 
	 * @author prade
	 * @param ifscCode
	 * @return
	 */

	public BankAddressModel getBankAddressByIfsc(String ifscCode) throws ClientWebApplicationException {
		BankAddressModel model = null;
		try {
			String message = commonRestService.getBankAddressByIfsc(ifscCode);
			ObjectMapper om = new ObjectMapper();
			model = om.readValue(message, BankAddressModel.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
}

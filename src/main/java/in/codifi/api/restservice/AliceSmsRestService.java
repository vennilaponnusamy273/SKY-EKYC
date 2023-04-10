package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;

@ApplicationScoped
public class AliceSmsRestService {
	@Inject
	@RestClient
	IAliceSmsRestService aliceSmsService;

	@Inject
	ApplicationProperties props;

	/**
	 * Method to send otp to Mobile Number
	 * 
	 * @author Nila
	 * @param otp
	 * @param mobile Number
	 * @return
	 */

	public boolean sendOTPtoMobile(int otp, long mobileNumber) {
		try {
			String Text = "Dear User, " + otp
					+ " is your verification code as requested online, this code is valid for next 5 minutes. Regards-AliceBlue";
			Response message = aliceSmsService.sendAliceSms(props.getApiKey(), props.getSenderId(),
					String.valueOf(mobileNumber), Text);
			if (message.getStatus() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			throw new RuntimeException("Error sending SMS: " + e.getMessage());
		}
	}
}

package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-sms")
@RegisterClientHeaders
public interface ISmsRestService {
	/**
	 * method to send otp for sms
	 * 
	 * @author Nila
	 * @param feedId
	 * @param senderId
	 * @param userName
	 * @param password
	 * @param mobileNumber
	 * @param message
	 * @return
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String SendSms(@QueryParam("feedid") String feedId, @QueryParam("senderid") String senderId,
			@QueryParam("username") String userName, @QueryParam("password") String password,
			@QueryParam("To") String mobileNumber, @QueryParam("Text") String message);

	/**
	 * method to send IVR link as sms
	 * 
	 * @author Nila
	 * @param feedId
	 * @param senderId
	 * @param userName
	 * @param password
	 * @param mobileNumber
	 * @param message
	 * @return
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String SendLink(@QueryParam("feedid") String feedId, @QueryParam("senderid") String senderId,
			@QueryParam("username") String userName, @QueryParam("password") String password,
			@QueryParam("To") String mobileNumber, @QueryParam("Text") String message);
}

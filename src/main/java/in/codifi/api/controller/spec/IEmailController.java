package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.springframework.web.bind.annotation.RequestBody;

import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;

public interface IEmailController {
	/**
	 * test Method
	 */
	@Path("/testMethod")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Test")
	public ResponseModel test();

	/**
	 * Method to send otp to Email Address
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	@Path("/sendMailOtp")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to send OTP to validate Email Id")
	public ResponseModel sendMailOtp(@RequestBody UserEntity userEntity);

	/**
	 * Method to validate sms OTP
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	@Path("/verifySmsOtp")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to verify email Otp")
	public ResponseModel verifyEmailOtp(@RequestBody UserEntity userEntity);

}

package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;

public interface IUserController {

	/**
	 * test Method
	 */
	@Path("/testMethod")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Test")
	public ResponseModel test();

	/**
	 * Method to send otp to mobile number
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	@Path("/sendSmsOtp")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to send OTP to validate Mobile Number")
	public ResponseModel sendSmsOtp(@RequestBody ApplicationUserEntity userEntity);

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
	@APIResponse(description = "Method to verify Mobile Otp")
	public ResponseModel verifySmsOtp(@RequestBody ApplicationUserEntity userEntity);

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
	public ResponseModel sendMailOtp(@RequestBody ApplicationUserEntity userEntity);

	/**
	 * Method to validate sms OTP
	 * 
	 * @author prade
	 * @param userEntity
	 * @return
	 */
	@Path("/verifyEmailOtp")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to verify email Otp")
	public ResponseModel verifyEmailOtp(@RequestBody ApplicationUserEntity userEntity);

	/**
	 * Method to save pan id to get details
	 * 
	 * @author Vennila Ponnusamy
	 * @param pan
	 * @return
	 */
	@Path("/getPan")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to pan id to get  details")
	public ResponseModel getPanDetails(@RequestBody ApplicationUserEntity userEntity);

	/**
	 * Method to save DOB
	 * 
	 * @author prade
	 * @param pan
	 * @return
	 */
	@Path("/saveDOB")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save DOB")
	public ResponseModel saveDob(@RequestBody ApplicationUserEntity userEntity);

	/**
	 * Method to intialize digilocker
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/iniDigilocker")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to intialize Digilocker")
	public ResponseModel iniDigilocker(@NotNull @QueryParam("applicationId") long applicationId);

	/**
	 * Method to save address
	 * 
	 * @author Vennila Ponnusamy
	 * @param code
	 * @param state
	 * @return
	 */
	@Path("/saveDigi")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save Address from Digilocker")
	public ResponseModel saveDigi(@NotNull @QueryParam("code") String code, @NotNull @QueryParam("state") String state,
			@NotNull @QueryParam("applicationId") long applicationId);
}

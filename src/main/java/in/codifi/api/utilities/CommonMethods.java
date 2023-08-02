package in.codifi.api.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.EmailLogEntity;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.entity.ErrorLogEntity;
import in.codifi.api.entity.ReqResEntity;
import in.codifi.api.entity.SmsLogEntity;
import in.codifi.api.model.AddressModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.EmailLogRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.repository.ErrorLogRepository;
import in.codifi.api.repository.ReqResRepository;
import in.codifi.api.repository.SmsLogRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
@SuppressWarnings("null")
public class CommonMethods {
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	@Inject
	ApplicationProperties props;
	@Inject
	Mailer mailer;
	@Inject
	ApplicationUserRepository repos;
	@Inject
	ReqResRepository reqResRepository;
	@Inject
	EmailTemplateRepository emailTemplateRepository;
	@Inject
	ApplicationUserRepository repository;

	@Inject
	ErrorLogRepository errorLogRepository;
	@Inject
	SmsLogRepository smsLogRepository;

	@Inject
	EmailLogRepository emailLogRepository;

	/**
	 * Method to generate OTP for Mobile number
	 * 
	 * @author prade
	 * @param mobileNumber
	 * @return
	 */
	public int generateOTP(long mobileNumber) {
		int otp = 000000;
		if (mobileNumber == 1234567890 || mobileNumber == 1111100000) {
			otp = 000000;
		} else {
			otp = (int) (Math.random() * 900000) + 100000;
		}
		System.out.println("OTP : " + otp);
		return otp;
	}

	/**
	 * Method to construct Failed method
	 * 
	 * @author prade
	 * @param failesMessage
	 * @return
	 */
	public ResponseModel constructFailedMsg(String failesMessage) {
		ResponseModel model = new ResponseModel();
		model.setStat(EkycConstants.FAILED_STATUS);
		model.setMessage(EkycConstants.FAILED_MSG);
		model.setReason(failesMessage);
		return model;
	}

	/**
	 * Method to send OTP to aliceBlue
	 * 
	 * @author Dinesh
	 * @param otp
	 * @param mobile
	 * @return
	 */

	public boolean sendOTPMessage(String otp, String mobile) {
		try {
			HttpURLConnection conn = null;
			JSONObject json = new JSONObject();
			json.put("apikey", props.getApiKey());
			json.put("senderid", props.getSenderId());
			json.put("number", mobile);
			json.put("message", "Dear User, " + otp
					+ " is your verification code as requested online, this code is valid for next 5 minutes. Regards-AliceBlue");
			URL url = new URL(props.getUrl());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = json.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br1.readLine()) != null) {
				@SuppressWarnings("unused")
				Object object = JSONValue.parse(output);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Inject
	public void MailService(Mailer javaMailSender) {
		this.mailer = javaMailSender;
	}

	/**
	 * Method to send mail
	 * 
	 * @param user
	 * @return
	 **/
	public void sendMailOtp(int otp, String emailId) throws MessagingException {
		EmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("otp");
		try {
			// check if email template is null or empty
			if (emailTempentity == null && emailTempentity.getBody() == null || emailTempentity.getSubject() == null) {
				SendMailOTP(otp, emailId);
			} else {
				String body_Message = emailTempentity.getBody();
				String body = body_Message.replace("{otp}", String.format("%06d", otp));
				String subject = emailTempentity.getSubject().replace("{otp}", String.format("%06d", otp));
				Mail mail = Mail.withHtml(emailId, subject, body);
				mailer.send(mail);
				System.out.println("The email was sent in Template: " + mail);
				storeEmailLog(body, subject, "The email was sent in Template: " + mail, "sendMailOtp", emailId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendErrorMail(String errorMessage, String errorCode) {
		EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("error");
		if (emailTemplateEntity != null && emailTemplateEntity.getBody() != null
				&& emailTemplateEntity.getSubject() != null && emailTemplateEntity.getToAddress() != null) {
			String bodyMessage = emailTemplateEntity.getBody();
			String body = bodyMessage.replace("{errorMessage}", errorMessage).replace("{errorCode}", errorCode);
			String subject = emailTemplateEntity.getSubject();
			Mail mail = Mail.withHtml(emailTemplateEntity.getToAddress(), subject, body);

			if (emailTemplateEntity.getCc() != null) {
				String[] ccAddresses = emailTemplateEntity.getCc().split(",");
				for (String ccAddress : ccAddresses) {
					mail = mail.addCc(ccAddress.trim());
				}
			}
			mailer.send(mail);
			storeEmailLog(body, subject, "The email was sent in error message: " + mail, "sendErrorMail",
					emailTemplateEntity.getToAddress());
			System.out.println("The email was sent in error message: " + mail);
		}
	}

	public void SendMailOTP(int otp, String emailId) {
		String body_Message = "<html><body><div style=\"background-color:#9eb0b747;width:70%\">"
				+ "<br><div class=\"container\" style=\"background-color: white; margin:30px; padding: 15px\">"
				+ "<div style=\"display:flex;justify-content:center\"><img src=\"https://web.nidhihq.com/assets/nidhi-logo.a16a42d3.svg\" width=\"150\" height=\"50\"/>"
				+ "</div><br /><div style=\"font-weight: bold; text-align: center; font-size: 20px\">OTP - {otp} </div>"
				+ "<br /><br /><div>Dear user,</div><br /><div>Your one-time password (OTP) is<span style=\"font-weight: bold\"> {otp} </span>.</div>"
				+ " <br /><div>Copy-paste the above OTP to log in to your account.</div> <br /> <div>This OTP is valid for 5 minutes only &amp; usable only once.</div> <br /><div>Regards,</div>"
				+ "<div style=\"font-weight: bold; display: flex\"><img src=\"https://web.nidhihq.com/assets/nidhi-logo.a16a42d3.svg\" width=\"55\" height=\"30\" /> </div><br /></div><div style=\"color: green; text-align: center\">"
				+ "<div>ⓒ Sky Commodities India Pvt Ltd</div><br /><div>About Us | Terms &amp; Conditions | Privacy Policy</div> <br /><div> 1st Floor, Proms Complex, SBI Colony, 1A Koramangala, 560034</div>  <br /> "
				+ "<div>This is an auto-generated email. You received this email because you are subscribed to NIDHI.</div><br /><div>Need assistance? Visit our <a href=\"https://web.nidhihq.com/\"> help center </a>."
				+ "</div><br><br></div></div></div></body></html>";
		String body = body_Message.replace("{otp}", String.format("%06d", otp));
		String getSubject = "{otp} is your OTP for email verification on Nidhi";
		String subject = getSubject.replace("{otp}", String.format("%06d", otp));
		Mail mail = Mail.withHtml(emailId, subject, body);
		mailer.send(mail);
		storeEmailLog(body, subject, "The email was sent: " + mail, "SendMailOTP", emailId);
		System.out.println("The email was sent: " + mail);
	}

	/**
	 * Trust Management
	 */
	public static void trustedManagement() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (

		NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to update step in Application Entity
	 * 
	 * @author Vennila Ponnusamy
	 * @param otp
	 * @param mobile Number
	 * @return
	 */

	public void UpdateStep(String step, Long ApplicationID) {
		try {
			Optional<ApplicationUserEntity> checkApplicationID = repos.findById(ApplicationID);
			if (checkApplicationID.isPresent()
					&& Double.parseDouble(checkApplicationID.get().getStage()) < Double.parseDouble(step)) {
				ApplicationUserEntity oldUserEntity = checkApplicationID.get();
				oldUserEntity.setStage(step);
				repos.save(oldUserEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to save out rest service request and response
	 * 
	 * @author prade
	 * @param req
	 * @param res
	 * @param applicationId
	 * @return
	 */
	public void reqResSaveObject(Object request, Object response, String type, long id) {
		ReqResEntity oldReqRes = reqResRepository.findByApplicationIdAndType(id, type);
		try {
			ObjectMapper mapper = new ObjectMapper();
			String req = mapper.writeValueAsString(request);
			String res = mapper.writeValueAsString(response);
			if (StringUtil.isNotNullOrEmpty(req) && StringUtil.isNotNullOrEmpty(res)
					&& StringUtil.isNotNullOrEmpty(type) && id > 0) {
				ReqResEntity savingResult = new ReqResEntity();
				savingResult.setApplicationId(id);
				savingResult.setType(type);
				savingResult.setRequest(req);
				savingResult.setResponse(res);
				if (oldReqRes != null) {
					savingResult.setId(oldReqRes.getId());
				}
				reqResRepository.save(savingResult);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to send IPV Link to email
	 * 
	 * @param Url
	 * @param mobileNumber
	 */
	public void sendMailIvr(String generateShortLink1, String emailId) throws MessagingException {
		EmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("ivr");
		String body_Message = emailTempentity.getBody();
		String body = body_Message.replace("{generateShortLink1}", generateShortLink1);
		String subject = emailTempentity.getSubject();
		Mail mail = Mail.withHtml(emailId, subject, body);
		mailer.send(mail);
		storeEmailLog(body, subject, "The email was sent: " + mail, "sendMailIvr", emailId);
	}

	/**
	 * Method to find Address via pincode
	 * 
	 * @author prade
	 * @param ifscCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AddressModel> findAddressByPinCode(String pincode) {
		List<AddressModel> model = null;
		try {
			URL url = new URL(props.getAddressFetchUrl() + pincode);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				return model;
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;

			while ((output = br1.readLine()) != null) {
				ObjectMapper om = new ObjectMapper();
				model = om.readValue(output, ArrayList.class);
			}
		} catch (Exception e) {
			return model;
		}
		return model;
	}

	/**
	 * Method to create bearer token
	 * 
	 * @author prade
	 * @return
	 */
	public String randomAlphaNumeric(Long mobileNumer, Long applicationId) {
		int count = 256;
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		builder.append(" ");
		builder.append(encrypt(mobileNumer.toString() + "_" + applicationId.toString()));
		return builder.toString();
	}

	public String encrypt(String value) {
		byte[] ivbuf = new byte[16];
		String output = null;
		try {
			IvParameterSpec iv = new IvParameterSpec(ivbuf);
			SecretKeySpec skeySpec = new SecretKeySpec(props.getTokenEncryptKey().getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			output = Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return output;
	}

	public String decrypt(String encrypted) {
		byte[] ivbuf = new byte[16];
		try {
			IvParameterSpec iv = new IvParameterSpec(ivbuf);
			SecretKeySpec skeySpec = new SecretKeySpec(props.getTokenEncryptKey().getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public ApplicationUserEntity generateAuthToken(ApplicationUserEntity updatedUserDetails) {
		String authToken = randomAlphaNumeric(updatedUserDetails.getMobileNo(), updatedUserDetails.getId());
		HazleCacheController.getInstance().getAuthToken().put(updatedUserDetails.getMobileNo().toString(), authToken,
				300, TimeUnit.SECONDS);
		updatedUserDetails.setAuthToken(authToken);
		return updatedUserDetails;
	}

	public void SaveLog(Long applicationId, String className, String methodName, String reason) {
		ErrorLogEntity errorLogEntity = errorLogRepository.findByApplicationIdAndClassNameAndMethodName(applicationId,
				className, methodName);
		if (errorLogEntity == null) {
			errorLogEntity = new ErrorLogEntity();
			errorLogEntity.setApplicationId(applicationId);
			errorLogEntity.setClassName(className);
			errorLogEntity.setMethodName(methodName);
		}
		errorLogEntity.setReason(reason);
		if (errorLogEntity != null) {
			errorLogRepository.save(errorLogEntity);
		}
	}

	public void sendEsignedMail(String mailIds, String name, String filePath, String fileName) {
		EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("Esign");
		if (emailTemplateEntity != null && emailTemplateEntity.getBody() != null
				&& emailTemplateEntity.getSubject() != null) {
			String bodyMessage = emailTemplateEntity.getBody();
			String body = bodyMessage.replace("{UserName}", name);
			String subject = emailTemplateEntity.getSubject();
			Mail mail = Mail.withHtml(mailIds, subject, body);
			File f = new File(filePath);
			String contentType = URLConnection.guessContentTypeFromName(fileName);
			mail.addAttachment(fileName, f, contentType);
			mailer.send(mail);
			storeEmailLog(body, subject, "The email was sent: " + mail, "sendEsignedMail", mailIds);
		}
	}

	/**
	 * Method to create smsLogMethod
	 * 
	 * @author Vennila
	 * @param SmsLogEntity
	 * @return
	 */

	public void storeSmsLog(String request, String smsResponse, String logMethod, long mobileNumber) {
		if (request == null || smsResponse == null || logMethod == null) {
			// Handle invalid input, such as throwing an IllegalArgumentException.
			throw new IllegalArgumentException("Request, smsResponse, or logMethod cannot be null.");
		}

		try {
			SmsLogEntity smsLogEntity = new SmsLogEntity();
			smsLogEntity.setMobileNo(mobileNumber);
			smsLogEntity.setLogMethod(logMethod);
			smsLogEntity.setRequestLog(request);
			smsLogEntity.setResponseLog(smsResponse);
			smsLogRepository.save(smsLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to create smsLogMethod
	 * 
	 * @author Vennila
	 * @param SmsLogEntity
	 * @return
	 */

	public void storeEmailLog(String message, String ReqSub, String emailResponse, String logMethod, String mailId) {
		if (message == null || emailResponse == null || logMethod == null) {
			throw new IllegalArgumentException("Request, EmailResponse, or logMethod cannot be null.");
		}

		try {
			EmailLogEntity emailLogEntity = new EmailLogEntity();
			emailLogEntity.setEmailId(mailId);
			emailLogEntity.setLogMethod(logMethod);
			emailLogEntity.setReqLogSub(ReqSub);
			emailLogEntity.setReqLog(message);
			emailLogEntity.setResponseLog(emailResponse);
			emailLogRepository.save(emailLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readUserNameFromCerFile(String certificateFilepath) {
		String userName = "";
		try (FileInputStream fis = new FileInputStream(certificateFilepath)) {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			Certificate cert = certFactory.generateCertificate(fis);
			X509Certificate x509Cert = (X509Certificate) cert;
			if (StringUtil.isNotNullOrEmpty(x509Cert.getSubjectDN().toString())) {
				String subject = x509Cert.getSubjectDN().toString();
				userName = StringUtil.substringAfter(subject, "CN=");
			}
			return userName;
		} catch (Exception e) {
			e.printStackTrace();
			return userName;
		}
	}

}

package in.codifi.api.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.entity.ReqResEntity;
import in.codifi.api.model.AddressModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.repository.ReqResRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
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
			System.out.println("OTP : " + otp);
		} else {
			otp = (int) (Math.random() * 900000) + 100000;
			System.out.println("OTP : " + otp);
		}
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
		String body_Message = emailTempentity.getBody();
		String body = body_Message.replace("{otp}", String.format("%06d", otp));
		String subject = emailTempentity.getSubject().replace("{otp}", String.format("%06d", otp));
		Mail mail = Mail.withHtml(emailId, subject, body);
		mailer.send(mail);
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
		System.out.println(builder.toString());
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

}

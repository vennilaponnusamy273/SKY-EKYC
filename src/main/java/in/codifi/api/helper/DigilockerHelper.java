package in.codifi.api.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.restservice.DigilockerRestService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class DigilockerHelper {
	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;
	@Inject
	AddressRepository addressRepository;
	@Inject
	DigilockerRestService digilockerRestService;

	private static final Logger logger = LogManager.getLogger(DigilockerHelper.class);
	/**
	 * Method to save address from digi
	 * 
	 * @author Vennila Ponnusamy
	 */
	public ResponseModel saveDigi(String code, String state, long applicationId) {
		ResponseModel responseModel = getDigiAcessToken(code, applicationId);
		return responseModel;
	}

	/**
	 * Method to save address from digi locker
	 * 
	 * @author Vennila Ponnusamy
	 * @param code
	 * @param applicationId
	 * @return
	 */
	public ResponseModel getDigiAcessToken(String code, long applicationId) {
		ResponseModel responseModel = null;
		JSONObject response = new JSONObject();
		try {
			String inputParameter = EkycConstants.DIGI_CONST_CODE + code + EkycConstants.DIGI_CONST_GRANDTYPE_CLIENTID
					+ props.getDigiClientId() + EkycConstants.DIGI_CONST_CLIENT_SECRET + props.getDigiSecret()
					+ EkycConstants.DIGI_CONST_REDIRECT_URL + props.getDigiRedirectUrl();
			CommonMethods.trustedManagement();
			URL url = new URL(props.getDigiBaseUrl() + EkycConstants.DIGI_CONST_TOKEN);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_URL_ENCODED);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = inputParameter.getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			}
			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 400)
					responseModel = commonMethods.constructFailedMsg(MessageConstants.AADHAR_TOKEN_400);
				else if (conn.getResponseCode() == 401) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.AADHAR_TOKEN_401);
				} else {
					responseModel = commonMethods.constructFailedMsg(Integer.toString(conn.getResponseCode()));
				}
			} else {
				BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//			String response1 = digil/ockerRestService.getAccessToken(code);
				String output;
				while ((output = br1.readLine()) != null) {
					Object object = JSONValue.parse(output);
					response = (JSONObject) object;
				}
				if (response != null) {
					if (response.containsKey(EkycConstants.DIGI_CONST_ACCESS_TOKEN)) {
						String accessToken = (String) response.get(EkycConstants.DIGI_CONST_ACCESS_TOKEN);
						responseModel = getXMlAadhar(accessToken, applicationId);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.ERR_NO_ACC_TOKEN);
					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERR_ACC_TOKEN);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId,"DigilockerHelper","getDigiAcessToken",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In getDigiAcessToken.for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods
					.constructFailedMsg(MessageConstants.ERR_NO_ACC_TOKEN + " - " + e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Merhod to get XML AAdhar
	 * 
	 * @author Vennila Ponnusamy
	 * @param accessToken
	 * @param applicationId
	 * @return
	 */
	public ResponseModel getXMlAadhar(String accessToken, long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		AddressEntity updatedAddEntity = null;
		try {
			CommonMethods.trustedManagement();
			String response = digilockerRestService.getXml(accessToken);
			if (StringUtil.isNotNullOrEmpty(response)) {
				org.json.JSONObject result = XML.toJSONObject(response);
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(result.toString());
				JSONObject jsonOutput = (JSONObject) obj;
				if (jsonOutput != null && jsonOutput.containsKey("KycRes")) {
					JSONObject kycResponse = (JSONObject) jsonOutput.get("KycRes");
					if (kycResponse != null && kycResponse.containsKey("UidData")) {
						JSONObject userDetails = (JSONObject) kycResponse.get("UidData");
						String AatharNo=(String) userDetails.get("uid");
						if (userDetails != null && userDetails.containsKey("Poa")) {
							JSONObject PoaDetails = (JSONObject) userDetails.get("Poa");
							if (applicationId >= 0) {
								AddressEntity checkExit = addressRepository.findByapplicationId(applicationId);
								if (checkExit == null) {
									AddressEntity entity = new AddressEntity();
									entity.setApplicationId(applicationId);
									entity.setIsdigi(1);
									entity.setAccessToken(accessToken);
									entity.setCo((String) PoaDetails.get("co"));
									if (PoaDetails.containsKey("house") && PoaDetails.get("house") instanceof Long) {
										entity.setFlatNo(PoaDetails.get("house").toString());
									} else {
										entity.setFlatNo((String) PoaDetails.get("house"));
									}
									entity.setAddress1((String) PoaDetails.get("vtc"));
									entity.setAddress2((String) PoaDetails.get("loc"));
									entity.setLandmark((String) PoaDetails.get("lm"));
									entity.setStreet((String) PoaDetails.get("street"));
									entity.setDistrict((String) PoaDetails.get("dist"));
									entity.setState((String) PoaDetails.get("state"));
									entity.setCountry((String) PoaDetails.get("country"));
									entity.setPincode((Long) PoaDetails.get("pc"));
									entity.setAatharNo(AatharNo);
									updatedAddEntity = addressRepository.save(entity);
								} else {
									if (PoaDetails.containsKey("house") && PoaDetails.get("house") instanceof Long) {
										checkExit.setFlatNo(PoaDetails.get("house").toString());
									} else {
										checkExit.setFlatNo((String) PoaDetails.get("house"));
									}
									checkExit.setCo((String) PoaDetails.get("co"));
									checkExit.setAccessToken(accessToken);
									checkExit.setIsdigi(1);
									checkExit.setAddress1((String) PoaDetails.get("vtc"));
									checkExit.setAddress2((String) PoaDetails.get("loc"));
									checkExit.setLandmark((String) PoaDetails.get("lm"));
									checkExit.setStreet((String) PoaDetails.get("street"));
									checkExit.setDistrict((String) PoaDetails.get("dist"));
									checkExit.setState((String) PoaDetails.get("state"));
									checkExit.setCountry((String) PoaDetails.get("country"));
									checkExit.setPincode((Long) PoaDetails.get("pc"));
									updatedAddEntity = addressRepository.save(checkExit);
								}
								if (updatedAddEntity != null) {
									commonMethods.UpdateStep(EkycConstants.PAGE_AADHAR, applicationId);
									responseModel = new ResponseModel();
									responseModel.setMessage(EkycConstants.SUCCESS_MSG);
									responseModel.setStat(EkycConstants.SUCCESS_STATUS);
									responseModel.setResult(updatedAddEntity);
									responseModel.setPage(EkycConstants.PAGE_PROFILE);
								} else {
									responseModel = commonMethods.constructFailedMsg(MessageConstants.ERR_SAVE_DIGI);
								}
							}
						} else {
							responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
						}
					}
					return responseModel;
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERR_NULL_DIGI);
			}
		} catch (ClientWebApplicationException e) {
			if (e.getResponse().getStatus() == 404)
				responseModel = commonMethods.constructFailedMsg(MessageConstants.AADHAR_NOT_AVAILABLE);
			else
				responseModel = commonMethods.constructFailedMsg(MessageConstants.AADHAR_INTERNAL_SERVER_ERR);
		} catch (Exception ex) {
			logger.error("An error occurred: " + ex.getMessage());
			commonMethods.SaveLog(applicationId,"DigilockerHelper","getXMlAadhar",ex.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In getXMlAadhar for the Error: " + ex.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(ex.getMessage());
		}
		return responseModel;
	}
}

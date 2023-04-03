package in.codifi.api.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class KRAHelper {
	@Inject
	ApplicationProperties properties;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	AddressRepository addressRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	KraKeyValueRepository keyValueRepository;

	/**
	 * Method to get the pan card status from the kra
	 * 
	 * @author prade
	 * @param xmlCode
	 * @return
	 */
	public JSONObject getPanCardStatus(String panCard) {
		try {
			String inputParameter = "panNo=" + panCard + "&userName=" + properties.getKraUsername() + "&PosCode="
					+ properties.getKraPosCode() + "&password=" + properties.getKraPassword() + "&PassKey=";
			CommonMethods.trustedManagement();
			URL url = new URL(properties.getKraPanStatusUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_URL_ENCODED);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = inputParameter.getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			}
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseMessage());
				throw new RuntimeException(MessageConstants.FAILED_HTTP_CODE + conn.getResponseCode());
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuilder sb = new StringBuilder();
			while ((output = br1.readLine()) != null) {
				sb.append(output);
			}
			System.out.println(sb.toString());
			/*
			 * covert xml into json
			 */
			JSONObject result = XML.toJSONObject(sb.toString());
			if (result != null) {
				JSONObject panRoot = (JSONObject) result.get(EkycConstants.CONST_KRA_APP_RES_ROOT);
				JSONObject panInformation = (JSONObject) panRoot.get(EkycConstants.CONST_KRA_APP_PAN_INQ);
				if (panInformation.has(EkycConstants.CONST_KRA_APP_NAME)) {
					return panInformation;
				} else {
					JSONObject errorResponse = (JSONObject) panInformation.get(EkycConstants.CONST_KRA_ERROR);
					return errorResponse;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to get Full Details
	 * 
	 * @param Pancard
	 * @param dob
	 * @return
	 */
	public JSONObject getPanCardDetails(String Pancard, String dob, int panCardStatus) {
		try {
			Date date1 = new SimpleDateFormat(EkycConstants.DATE_FORMAT).parse(dob);
			SimpleDateFormat formatter = new SimpleDateFormat(EkycConstants.KRA_DATE_FORMAT);
			String appKRACode = getKraDesc(panCardStatus);
			String actualData = formatter.format(date1);
			String xmlCode = "<APP_REQ_ROOT><APP_PAN_INQ><APP_PAN_NO>" + Pancard + "</APP_PAN_NO><APP_DOB_INCORP>"
					+ actualData + "</APP_DOB_INCORP><APP_POS_CODE>" + properties.getKraPosCode() + "</APP_POS_CODE>"
					+ "<APP_RTA_CODE>" + properties.getKraPosCode() + "</APP_RTA_CODE><APP_KRA_CODE>" + appKRACode
					+ "</APP_KRA_CODE><FETCH_TYPE>I</FETCH_TYPE>" + "</APP_PAN_INQ></APP_REQ_ROOT>";
			String request = "InputXML=" + xmlCode + "&username=" + properties.getKraUsername() + "&PosCode="
					+ properties.getKraPosCode() + "&password=" + properties.getKraPassword() + "&PassKey=";

			CommonMethods.trustedManagement();
			URL url = new URL(properties.getKraDetailsFetchUrl());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_URL_ENCODED);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			}
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseMessage());
				throw new RuntimeException(MessageConstants.FAILED_HTTP_CODE + conn.getResponseCode());
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuilder sb = new StringBuilder();
			while ((output = br1.readLine()) != null) {
				sb.append(output);
			}
			ObjectMapper xmlMapper = new XmlMapper();
			Object obj = xmlMapper.readValue(sb.toString(), Object.class);
			ObjectMapper jsonMapper = new ObjectMapper();
//		        jsonMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN); // to write BigDecimal values as plain strings
			String jsonString = jsonMapper.writeValueAsString(obj);
			JSONObject jsonObject = new JSONObject(jsonString);
			System.out.println(jsonObject.toString());
			JSONObject kycData = null;
			if (jsonObject.has(EkycConstants.CONST_KYC_DATA)) {
				kycData = jsonObject.getJSONObject(EkycConstants.CONST_KYC_DATA);
				return kycData;
			} else {
				kycData = jsonObject.getJSONObject(EkycConstants.CONST_KRA_ERROR);
				return kycData;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to get details from KRA and update int the data base
	 * 
	 * @author GOWRI SANKAR R
	 * @param kraDetails
	 * @param pDto
	 * @return
	 */
	public ProfileEntity updateDetailsFromKRA(JSONObject kraDetails, Long applicationId) {
		ProfileEntity savedProfileEntity = null;
		try {
			ProfileEntity profileEntity = profileRepository.findByapplicationId(applicationId);
			if (profileEntity == null) {
				profileEntity = new ProfileEntity();
			}
			profileEntity.setApplicationId(applicationId);
			AddressEntity addressEntity = addressRepository.findByapplicationId(applicationId);
			if (addressEntity == null) {
				addressEntity = new AddressEntity();
			}
			addressEntity.setApplicationId(applicationId);
			/*
			 * 
			 */
			String appliCantName = kraDetails.getString("APP_NAME");
			String applicantFatherName = kraDetails.getString("APP_F_NAME");
			String applicantGender = kraDetails.getString("APP_GEN");
			String maritialStatus = kraDetails.getString("APP_MAR_STATUS");
			profileEntity.setApplicationId(applicationId);
			profileEntity.setApplicantName(appliCantName.toUpperCase());
			profileEntity.setFatherName(applicantFatherName.toUpperCase());
			/*
			 * set gender and tittle
			 */
			if (applicantGender.equalsIgnoreCase("M")) {
				profileEntity.setGender("MALE");
				profileEntity.setTitle("MR");
			} else {
				profileEntity.setGender("FEMALE");
				profileEntity.setTitle("MRS");
			}

			/**
			 * set martial status
			 */
			if (maritialStatus.equalsIgnoreCase("01")) {
				profileEntity.setMaritalStatus("Married");
			} else {
				profileEntity.setMaritalStatus("Single");
			}

			String corrsAdd1 = kraDetails.getString("APP_COR_ADD1");
			String corrsAdd2 = kraDetails.getString("APP_COR_ADD2");
			String corrsAdd3 = kraDetails.getString("APP_COR_ADD3");
			String corrsCity = kraDetails.getString("APP_COR_CITY");
			String corrsState = kraDetails.getString("APP_COR_STATE");
			String corrsCountry = kraDetails.getString("APP_COR_CTRY");
			int corrsPinCode = kraDetails.optInt("APP_COR_PINCD");
			/*
			 * Set communication adders from the KRA
			 */
			addressEntity.setKraAddress1(corrsAdd1);
			addressEntity.setKraAddress2(corrsAdd2);
			addressEntity.setKraAddress3(corrsAdd3);
			addressEntity.setKraCity(corrsCity);
			addressEntity.setKraPin(corrsPinCode);

			addressEntity.setKraState(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.STATEKEY + corrsState));
			addressEntity.setKraCountry(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.COUNTRYKEY + corrsCountry));
			String perAddress1 = kraDetails.getString("APP_PER_ADD1");
			String perAddress2 = kraDetails.getString("APP_PER_ADD2");
			String perAddress3 = kraDetails.getString("APP_PER_ADD3");
			String perCity = kraDetails.getString("APP_PER_CITY");
			String perState = kraDetails.getString("APP_PER_STATE");
			String perCountry = kraDetails.getString("APP_PER_CTRY");
			int perPinCode = kraDetails.optInt("APP_PER_PINCD");
			/*
			 * Set Permanent address from the KRA
			 */
			addressEntity.setKraPerAddress1(perAddress1);
			addressEntity.setKraPerAddress2(perAddress2);
			addressEntity.setKraPerAddress3(perAddress3);
			addressEntity.setKraPerCity(perCity);
			addressEntity.setKraPerPin(perPinCode);
			addressEntity.setKraPerState(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.STATEKEY + perState));
			addressEntity.setKraPerCountry(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.COUNTRYKEY + perCountry));
			String corrsIncome = kraDetails.getString("APP_INCOME");
			String corrsOcc = kraDetails.getString("APP_OCC");
			String corrsPolConn = kraDetails.getString("APP_POL_CONN");
			if (StringUtil.isNotNullOrEmpty(corrsIncome)) {
				profileEntity.setAnnualIncome(
						HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.INCOMEKEY + corrsIncome));
			}
			if (StringUtil.isNotNullOrEmpty(corrsOcc)) {
				profileEntity.setOccupation(HazleCacheController.getInstance().getKraKeyValue()
						.get(EkycConstants.OCCUPATIONKEY + corrsOcc));
			}

			if (StringUtil.isNotNullOrEmpty(corrsPolConn)) {
				profileEntity.setPoliticalExposure(
						HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.PEPKEY + corrsPolConn));
			}
			savedProfileEntity = profileRepository.save(profileEntity);
			if (savedProfileEntity != null) {
				commonMethods.UpdateStep(EkycConstants.PAGE_PAN_KRA_DOB_ENTRY, applicationId);
			}
			addressEntity.setIsKra(1);
			addressRepository.save(addressEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savedProfileEntity;

	}

	public String getKraDesc(int appStatus) {
		String appKraCode = "CVLKRA";
		if (appStatus == 002 || appStatus == 007 || appStatus == 2 || appStatus == 7) {
			appKraCode = "CVLKRA";
		} else if (appStatus == 102 || appStatus == 107) {
			appKraCode = "NDML";
		} else if (appStatus == 202 || appStatus == 207) {
			appKraCode = "DOTEX";
		} else if (appStatus == 302 || appStatus == 307) {
			appKraCode = "CAMS";
		} else if (appStatus == 402 || appStatus == 407) {
			appKraCode = "KARVY";
		} else if (appStatus == 502 || appStatus == 507) {
			appKraCode = "BSE";
		}
		return appKraCode;
	}

}

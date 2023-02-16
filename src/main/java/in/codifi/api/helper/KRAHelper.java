package in.codifi.api.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Service
public class KRAHelper {
	@Inject
	ApplicationProperties properties;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	AddressRepository addressRepository;
	@Inject
	CommonMethods commonMethods;

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
	public JSONObject getPanCardDetails(String Pancard, String dob) {
		try {
			Date date1 = new SimpleDateFormat(EkycConstants.DATE_FORMAT).parse(dob);
			SimpleDateFormat formatter = new SimpleDateFormat(EkycConstants.KRA_DATE_FORMAT);
			String actualData = formatter.format(date1);
			String xmlCode = "<APP_REQ_ROOT><APP_PAN_INQ><APP_PAN_NO>" + Pancard + "</APP_PAN_NO><APP_DOB_INCORP>"
					+ actualData + "</APP_DOB_INCORP><APP_POS_CODE>" + properties.getKraPosCode() + "</APP_POS_CODE>"
					+ "<APP_RTA_CODE>" + properties.getKraPosCode()
					+ "</APP_RTA_CODE><APP_KRA_CODE>CVLKRA</APP_KRA_CODE><FETCH_TYPE>I</FETCH_TYPE>"
					+ "</APP_PAN_INQ></APP_REQ_ROOT>";
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
				System.out.println(output);
			}
			JSONObject xmlJSONObj = XML.toJSONObject(sb.toString());
			if (xmlJSONObj != null) {
				JSONObject rootJson = xmlJSONObj.getJSONObject(EkycConstants.CONST_KYC_ROOT);
				JSONObject kycData = null;
				if (rootJson.has(EkycConstants.CONST_KYC_DATA)) {
					kycData = rootJson.getJSONObject(EkycConstants.CONST_KYC_DATA);
					return kycData;
				} else {
					kycData = rootJson.getJSONObject(EkycConstants.CONST_KRA_ERROR);
					return kycData;
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
//			String corrsState = kraDetails.getString("APP_COR_STATE");
			// String corrsCountry = kraDetails.getString("APP_COR_CTRY");
			int corrsPinCode = kraDetails.getInt("APP_COR_PINCD");
			/*
			 * Set communication adders from the KRA
			 */
			addressEntity.setKraAddress1(corrsAdd1);
			addressEntity.setKraAddress2(corrsAdd2);
			addressEntity.setKraAddress3(corrsAdd3);
			addressEntity.setKraCity(corrsCity);
			addressEntity.setKraPin(corrsPinCode);
//		org.json.JSONObject corssState = EKycDAO.getInstance().getKeyValueForKRA(EkycConstants.STATE_CODE, corrsState);
//		pdto.setState(corssState.getString("value"));

			String perAddress1 = kraDetails.getString("APP_PER_ADD1");
			String perAddress2 = kraDetails.getString("APP_PER_ADD2");
			String perAddress3 = kraDetails.getString("APP_PER_ADD3");
			String perCity = kraDetails.getString("APP_PER_CITY");
//			String perState = kraDetails.getString("APP_PER_STATE");
			// String perCountry = kraDetails.getString("APP_PER_CTRY");
			int perPinCode = kraDetails.getInt("APP_PER_PINCD");
			/*
			 * Set Permanent address from the KRA
			 */
			addressEntity.setKraPerAddress1(perAddress1);
			addressEntity.setKraPerAddress2(perAddress2);
			addressEntity.setKraPerAddress3(perAddress3);
			addressEntity.setKraPerCity(perCity);
			addressEntity.setKraPerPin(perPinCode);
//			org.json.JSONObject permnanentState = EKycDAO.getInstance().getKeyValueForKRA(EkycConstants.STATE_CODE,
//			perState);
//	pdto.setPer_state(permnanentState.getString("value"));
			savedProfileEntity = profileRepository.save(profileEntity);
			if (savedProfileEntity != null) {
				commonMethods.UpdateStep(3, applicationId);
			}
			addressEntity.setIsKra(1);
			addressRepository.save(addressEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savedProfileEntity;

	}

}

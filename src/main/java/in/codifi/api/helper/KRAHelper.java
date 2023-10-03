package in.codifi.api.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.repository.AccessLogManager;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.restservice.KraPanRestService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
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
	@Inject
	KraPanRestService kraPanRestService;
	@Inject
	ApplicationUserRepository repository;
	@Inject
	AccessLogManager accessLogManager;

	private static final Logger logger = LogManager.getLogger(KRAHelper.class);
	private static String OS = System.getProperty("os.name").toLowerCase();

	/**
	 * Method to get the pan card status from the kra
	 * 
	 * @author prade
	 * @param xmlCode
	 * @return
	 */
	public JSONObject getPanCardStatus(String panCard, Long id) {
		try {
			CommonMethods.trustedManagement();
			/*
			 * covert xml into json
			 */
			String panStatus = kraPanRestService.getpanStatus(panCard);
			accessLogManager.insertRestAccessLogsIntoDB(Long.toString(id), panCard, panStatus, "getPanCardStatus",
					"/pan/saveDOB");
			JSONObject result = XML.toJSONObject(panStatus);
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
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "KRAHelper", "getPanCardStatus", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In getPanCardStatus for the Error: "
							+ e.getMessage(),
					"ERR-001");
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
	public JSONObject getPanCardDetails(String Pancard, String dob, int panCardStatus, long applicationId) {
		try {
			Date date1 = new SimpleDateFormat(EkycConstants.DATE_FORMAT).parse(dob);
			SimpleDateFormat formatter = new SimpleDateFormat(EkycConstants.KRA_DATE_FORMAT);
			String appKRACode = getKraDesc(panCardStatus);
			String actualData = formatter.format(date1);
			String xmlCode = "<APP_REQ_ROOT><APP_PAN_INQ><APP_PAN_NO>" + Pancard + "</APP_PAN_NO><APP_DOB_INCORP>"
					+ actualData + "</APP_DOB_INCORP><APP_POS_CODE>" + properties.getKraPosCode() + "</APP_POS_CODE>"
					+ "<APP_RTA_CODE>" + properties.getKraPosCode() + "</APP_RTA_CODE><APP_KRA_CODE>" + appKRACode
					+ "</APP_KRA_CODE><FETCH_TYPE>I</FETCH_TYPE>" + "</APP_PAN_INQ></APP_REQ_ROOT>";
			CommonMethods.trustedManagement();
			String result = kraPanRestService.getPanKra(xmlCode);
			accessLogManager.insertRestAccessLogsIntoDB(Long.toString(applicationId), xmlCode, result,
					"getPanCardDetails", "/pan/saveDOB");
			writeXmlFile(result, applicationId, Pancard);
			ObjectMapper xmlMapper = new XmlMapper();
			Object obj = xmlMapper.readValue(result, Object.class);
			ObjectMapper jsonMapper = new ObjectMapper();
			String jsonString = jsonMapper.writeValueAsString(obj);
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject kycData = null;
			updateKraDate(result, applicationId);
			if (jsonObject.has(EkycConstants.CONST_KYC_DATA)) {
				kycData = jsonObject.getJSONObject(EkycConstants.CONST_KYC_DATA);
				return kycData;
			} else {
				kycData = jsonObject.getJSONObject(EkycConstants.CONST_KRA_ERROR);
				return kycData;
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "KRAHelper", "getPanCardDetails", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In getPanCardDetails for the Error: "
							+ e.getMessage(),
					"ERR-001");
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
	public ProfileEntity updateDetailsFromKRA(JSONObject kraDetails, Long applicationId, int panCardStatus) {
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
			String kycID = kraDetails.getString("APP_COR_ADD_PROOF");
			String kycIDNumber = kraDetails.getString("APP_PER_ADD_REF");
			addressEntity.setKraaddressproof(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.ADDRESS_PROOF + kycID));
			if (StringUtil.isEqual(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.ADDRESS_PROOF + kycID),
					"AADHAAR")) {
				addressEntity.setKraproofIdNumber(addCharAtAadhar(kycIDNumber));
			} else {
				addressEntity.setKraproofIdNumber(kycIDNumber);
			}
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
			/**if (checkAppStatus(panCardStatus)) {
				addressEntity.setIsKra(1);
				if (StringUtil.isEqual(
						HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.ADDRESS_PROOF + kycID),
						"AADHAAR")) {
					String AatharNumber = addCharAtAadhar(kycIDNumber);
					// System.out.println("the kycIDNumber" + AatharNumber);
					if (AatharNumber.length() >= 4) {
						String lastFourDigits = AatharNumber.substring(AatharNumber.length() - 4);
						if (lastFourDigits.matches("\\d{4}")) {
							addressEntity.setIsKra(1);
						} else {
							addressEntity.setIsKra(0);
						}
					}
				} else {
					if (kycIDNumber == null || kycIDNumber.isEmpty() || kycIDNumber.equals("NA")) {
						addressEntity.setIsKra(0);
					}
				}
			} else {
				addressEntity.setIsKra(0);
			}**/
			addressEntity.setIsKra(0);
			addressRepository.save(addressEntity);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "KRAHelper", "updateDetailsFromKRA", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In updateDetailsFromKRA for the Error: "
							+ e.getMessage(),
					"ERR-001");
		}
		return savedProfileEntity;

	}

	public boolean checkAppStatus(int appStatuscode) {
		boolean isPresent = false;
		if (appStatuscode == 7 || appStatuscode == 007 || appStatuscode == 107 || appStatuscode == 207
				|| appStatuscode == 307 || appStatuscode == 407 || appStatuscode == 507) {
			isPresent = true;
		}
		return isPresent;
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

	public String addCharAtAadhar(String value) {
		int limit = 12;
		if (value.length() >= limit) {
			return value;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < limit - value.length(); i++) {
			builder.append("X");
		}
		builder.append(value);
		return builder.toString();
	}

	private void writeXmlFile(String result, long applicationId, String Pancard) {
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		try {
			String baseFilePath = properties.getFileBasePath() + applicationId;
			File directory = new File(baseFilePath);
			directory.mkdirs();
			String xmlFileName = baseFilePath + slash + Pancard + "_PAN" + EkycConstants.XML_EXTENSION;
			FileWriter fileWriter = new FileWriter(xmlFileName);
			fileWriter.write(result);
			fileWriter.close();
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "KRAHelper", "writeCmlFile", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In writeCmlFile for the Error: " + e.getMessage(),
					"ERR-001");
		}

	}

	public void updateKraDate(String result, long applicationId) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(result)));
			NodeList appResponseDateNodes = document.getElementsByTagName("APP_RESPONSE_DATE");
			if (appResponseDateNodes.getLength() > 0) {
				Element appResponseDateElement = (Element) appResponseDateNodes.item(0);
				String appResponseDateStr = appResponseDateElement.getTextContent();
				Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
				if (isUserPresent.isPresent()) {
					ApplicationUserEntity oldUserEntity = isUserPresent.get();
					oldUserEntity.setKraResponseDate(appResponseDateStr);
					repository.save(oldUserEntity);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "KRAHelper", "updateKraDate", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request, In updateKraDate for the Error: "
							+ e.getMessage(), "ERR-001");
		}
	}

}

package in.codifi.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DigioEntity;
import in.codifi.api.helper.DigioHelper;
import in.codifi.api.model.DigioAction;
import in.codifi.api.model.DigioIniResponseModel;
import in.codifi.api.model.DigioRequestModel;
import in.codifi.api.model.DigioSaveAddResponse;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.WebhookDigioRequestModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DigioRepository;
import in.codifi.api.restservice.DigioRestService;
import in.codifi.api.service.spec.IDigioService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class DigioService implements IDigioService {
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	DigioRestService digioRestService;
	@Inject
	DigioRepository digioRepository;
	@Inject
	ApplicationProperties props;
	@Inject
	DigioHelper digioHelper;
	private static final Logger logger = LogManager.getLogger(DigioService.class);

	/**
	 * Method to intialize digio to open digi locker
	 */

	@Override
	public ResponseModel iniDigio(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(applicationId);
			if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0
					&& user.get().getMobileNo() != null) {
				DigioRequestModel digioIniRequest = constructDigiIniRequest(user.get());
				if (digioIniRequest != null) {
					DigioIniResponseModel iniResponseModel = digioRestService.digioInitialize(digioIniRequest);
					if (iniResponseModel != null && StringUtil.isNotNullOrEmpty(iniResponseModel.getId())) {
						String randomKey = commonMethods.randomAlphaNumericNew(6);
						String url = props.getDigioBaseUrl() + iniResponseModel.getId() + "/" + randomKey + "/"
								+ Long.toString(user.get().getMobileNo()) + "/" + props.getDigioFinalUrl();
						DigioEntity digioEntity = new DigioEntity();
						digioEntity.setApplicationId(applicationId);
						digioEntity.setMobileNo(Long.toString(user.get().getMobileNo()));
						digioEntity.setRandomKey(randomKey);
						digioEntity.setIniResjson(mapper.writeValueAsString(iniResponseModel));
						digioEntity.setRequestUrl(url);
						digioEntity.setRequestId(iniResponseModel.getId());
						DigioEntity oldRecord = digioRepository.findByapplicationId(applicationId);
						if (oldRecord != null && oldRecord.getId() > 0) {
							digioRepository.deleteById(oldRecord.getId());
						}
						DigioEntity updatedEntity = digioRepository.save(digioEntity);
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
						responseModel.setResult(updatedEntity.getRequestUrl());
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.DIGIO_INI_RES_NULL);
					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.DIGIO_REQ_FAILED);
				}
			} else {
				if (user.isEmpty()) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "DigioService", "saveBank", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request. In iniDigio for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to construct json for inti digio
	 * 
	 * @author prade
	 * 
	 * @param applicationUserEntity
	 * @return
	 */
	private DigioRequestModel constructDigiIniRequest(ApplicationUserEntity applicationUserEntity) {
		DigioRequestModel requestModel = new DigioRequestModel();
		requestModel.setCustomerIdentifier(Long.toString(applicationUserEntity.getMobileNo()));
		DigioAction action = new DigioAction();
		action.setType("DIGILOCKER");
		action.setTitle("DIGILOCKER KYC");
		action.setDescription("Please share your aadhaar card and Pan from digilocker");
		action.setStrictValidationType("AADHAAR");
		action.setIdAnalysisRequired(true);
		action.setAllowOcrDataUpdate(true);
		action.setFaceMatchObjType("MATCH_REQUIRED");
		List<String> docList = new ArrayList<>();
		docList.add("AADHAAR");
		docList.add("PAN");
		action.setDocumentTypes(docList);
		List<DigioAction> listActions = new ArrayList<>();
		listActions.add(action);
		requestModel.setActions(listActions);
		requestModel.setNotifyCustomer(false);
		requestModel.setGenerateAccessToken(true);
		return requestModel;
	}

	/**
	 * Method to save address from digio
	 */
	@Override
	public ResponseModel saveDigioAadhar(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			System.out.println("the whDigilocker 4");
			DigioEntity savedIniDigio = digioRepository.findByapplicationId(applicationId);
			if (savedIniDigio != null && StringUtil.isNotNullOrEmpty(savedIniDigio.getRequestId())) {
				DigioSaveAddResponse addResponse = digioRestService.saveDigiAddress(savedIniDigio.getRequestId());
				if (addResponse != null) {
					System.out.println("the whDigilocker 5");
					responseModel = digioHelper.saveAddFromDigio(applicationId, addResponse);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "DigioService", "saveDigioAadhar", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request. In saveDigioAadhar for the Error: "
							+ e.getMessage(), "ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
			e.printStackTrace();
		}
		return responseModel;
	}

	/**
	 * Method for web hook to check status for digi locker
	 */
	@Override
	public ResponseModel whDigilocker(WebhookDigioRequestModel digioRequestModel) {
		ResponseModel responseModel = new ResponseModel();
		try {
			System.out.println("the whDigilocker 1");
			if (digioRequestModel != null && digioRequestModel.getPayload() != null
					&& digioRequestModel.getPayload().getDigilockerRequest() != null
					&& StringUtil.isNotNullOrEmpty(
							digioRequestModel.getPayload().getDigilockerRequest().getCustomerIdentifier())
					&& StringUtil.isNotNullOrEmpty(
							digioRequestModel.getPayload().getDigilockerRequest().getKycRequestId())) {
				System.out.println("the whDigilocker 2");
				String mobileNo = digioRequestModel.getPayload().getDigilockerRequest().getCustomerIdentifier();
				String reqId = digioRequestModel.getPayload().getDigilockerRequest().getKycRequestId();
				DigioEntity oldEntity = digioRepository.findByMobileNoAndRequestId(mobileNo, reqId);
				if (oldEntity != null) {
					System.out.println("the whDigilocker 3");
					ObjectMapper mapper = new ObjectMapper();
					oldEntity.setWhResjson(mapper.writeValueAsString(digioRequestModel));
					digioRepository.save(oldEntity);
					saveDigioAadhar(oldEntity.getApplicationId());
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(Integer.toUnsignedLong(0), "DigioService", "whDigilocker", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request. In saveDigioAadhar for the Error: "
							+ e.getMessage(), "ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
			e.printStackTrace();
		}
		return responseModel;
	}
	/**
	 * Method to random Generated key
	 * 
	 * @author prade
	 * @param count
	 * @return
	 */
	public String randomAlphaNumericNew(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
}

package in.codifi.api.service;

import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.CamsEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.KraKeyValueEntity;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.model.BankAddressModel;
import in.codifi.api.model.CamsRedirectResModel;
import in.codifi.api.model.CamsResModel;
import in.codifi.api.model.PushDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.CamsRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.restservice.CamsRestService;
import in.codifi.api.restservice.RazorpayIfscRestService;
import in.codifi.api.service.spec.ICamsDocService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class CamsDocService implements ICamsDocService {

	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	CamsRestService camsRestService;
	@Inject
	CamsRepository camsRepository;
	@Inject
	DocumentRepository documentRepository;
	@Inject
	ApplicationUserRepository repository;
	@Inject
	BankRepository bankRepository;
	@Inject
	KraKeyValueRepository kraKeyValueRepository;
	@Inject
	RazorpayIfscRestService commonRestService;

	private static final Logger logger = LogManager.getLogger(CamsDocService.class);

	@Override
	public ResponseModel getCamsPdf(PushDataModel pushDataModel) {
		ResponseModel responseModel = new ResponseModel();
		System.out.println("The CAMS PDF is Running");
		try {
			// Extracting values from the JSON request
			String timestamp = pushDataModel.getTimestamp();
			String clienttxnid = pushDataModel.getClienttxnid();
			String pdfBase64 = pushDataModel.getDataDetail() != null
					? (!StringUtil.isNullOrEmpty(pushDataModel.getDataDetail().getPdfbase64()))
							? pushDataModel.getDataDetail().getPdfbase64()
							: ""
					: "";
//			String pdfBase64 = pushDataModel.getDataDetail().getPdfbase64();
			// Printing the extracted values
			System.out.println("Timestamp:" + timestamp);
			System.out.println("Client Transaction ID:" + clienttxnid);
			System.out.println("Client pdfBase64:" + pdfBase64);
			// Convert PushDataModel to JSON string
			ObjectMapper mapper = new ObjectMapper();
			String responseBody = mapper.writeValueAsString(pushDataModel);
			System.out.println("PushDataModel responseBody:" + responseBody);

			CamsEntity camsEntity = camsRepository.findByclientTxtId(clienttxnid);
			if (camsEntity != null) {
//				convertBase64ToPdf(camsEntity.getApplicationId(), pdfBase64);
				if (pdfBase64 != "") {
					convertBase64ToPdf(camsEntity.getApplicationId(), pdfBase64);
				}
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(timestamp);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.NONEED_TO_DOCUMENT);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request. In getCamsPdf for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	public void convertBase64ToPdf(long applicationId, String base64String) {
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;

			// Trim whitespace from the base64 string
			base64String = base64String.trim();
			// Remove all non-base64 characters
			base64String = base64String.replaceAll("[^A-Za-z0-9+/=]", "");
			// Decode the base64 string
			byte[] decodedBytes = Base64.getDecoder().decode(base64String);
			String fileName = applicationId + EkycConstants.UNDERSCORE + "INCOME_PROOF" + EkycConstants.PDF_EXTENSION;
			String totalFileName = props.getFileBasePath() + applicationId + slash + fileName;
			try (FileOutputStream fos = new FileOutputStream(totalFileName)) {
				fos.write(decodedBytes);
				System.out.println("PDF file successfully created at: " + totalFileName);
			}
			saveDocumentDetails(applicationId, fileName, totalFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveDocumentDetails(long applicationId, String fileName, String documentPath) {
		try {
			DocumentEntity oldEntity = documentRepository.findByApplicationIdAndDocumentType(applicationId,
					"INCOME_PROOF");

			if (oldEntity == null) {
				DocumentEntity documentEntity = new DocumentEntity();
				documentEntity.setApplicationId(applicationId);
				documentEntity.setAttachementUrl(documentPath);
				documentEntity.setAttachement(fileName);
				documentEntity.setDocumentType("INCOME_PROOF");
				documentEntity.setTypeOfProof("6 Month Bank Statement");
				documentRepository.save(documentEntity);
			} else {
				oldEntity.setAttachementUrl(documentPath);
				oldEntity.setAttachement(fileName);
				documentRepository.save(oldEntity);
			}
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				ApplicationUserEntity userEntity = isUserPresent.get();
				userEntity.setCamsIncome("Y"); // Corrected method name to setCamsIncome
				repository.save(userEntity); // Save the entity, not the Optional
			}
		} catch (Exception e) {
			logger.error("Error saving document details.", e);
		}
	}

	@Override
	public ResponseModel getCamsRedirectUrl(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		CamsRedirectResModel camsRedirectResModel = null;
		System.out.println("the CAMS Service is Running");
		try {
			String bankFid = null;
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(applicationId);
			SegmentEntity savedSegmentEntity = segmentRepository.findByapplicationId(applicationId);
			if (user.isPresent() && savedSegmentEntity != null && savedSegmentEntity.getEd() == 1
					|| savedSegmentEntity.getCd() == 1 || savedSegmentEntity.getComm() == 1) {
				BankAddressModel model = null;
//
//				BankEntity savedBankEntity = bankRepository.findByapplicationId(applicationId);
//				if (savedBankEntity != null) {
//					model = commonRestService.getBankAddressByIfsc(savedBankEntity.getIfsc());
//					if (model != null && model.getBank() != null) {
//						System.out.println("the model bank" + model.getBank());
//						List<KraKeyValueEntity> kraKeyValueEntity = kraKeyValueRepository
//								.findByMasterIdAndMasterName("14", "CAMS");
//
//						for (KraKeyValueEntity entity : kraKeyValueEntity) {
//							String bankName = model.getBank();
//							String kraValue = entity.getKraValue();
//							System.out.println("the bankName" + bankName);
//							System.out.println("the kraValue bankName" + kraValue);
//							if (kraValue != null && kraValue.toLowerCase().contains(bankName.toLowerCase())) {
//								bankFid = entity.getKraKey();
//								System.out.println("Match found: bankName is a substring of kraValue");
//								System.out.println("Setting bankFid: " + bankFid);
//								break; // Exiting loop once bankFid is found
//							}
//						}
//						if (bankFid == null) {
//							// If bankFid is not found, return an error message
//							responseModel = commonMethods.constructFailedMsg(MessageConstants.BANK_NAME_NULL);
//							return responseModel;
//						}
//					} else {
//						responseModel = commonMethods.constructFailedMsg(MessageConstants.BANK_NAME_NULL);
//					}
//				} else {
//					responseModel = commonMethods.constructFailedMsg(MessageConstants.BANK_NAME_NULL);
//				}

				System.out.println("the bankFid" + bankFid);
				CamsResModel camsResModel = camsRestService.findSessionIdAndToken();
				if (camsResModel != null && camsResModel.getSessionId() != null && camsResModel.getToken() != null) {
					camsRedirectResModel = camsRestService.finDirectUrl(applicationId, user.get().getMobileNo(),
							camsResModel.getToken(), camsResModel.getSessionId(), bankFid);
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setResult(camsRedirectResModel);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.NONEED_TO_DOCUMENT);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request. In getCamsPdf for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

}

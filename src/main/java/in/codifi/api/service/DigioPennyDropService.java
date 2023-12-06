package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PennyVerificationResponseEntity;
import in.codifi.api.model.PennyVerificationResModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.PennyVerificationRepository;
import in.codifi.api.restservice.PennyDropDigioService;
import in.codifi.api.service.spec.IDigioPennyDropService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class DigioPennyDropService implements IDigioPennyDropService {
	private static final Logger logger = LogManager.getLogger(DigioPennyDropService.class);

	@Inject
	BankRepository bankRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	PennyDropDigioService pennyDropDigioService;
	@Inject
	PennyVerificationRepository pennyVerificationRepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;

	@Override
	public ResponseModel createPennyDrop(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		PennyVerificationResponseEntity saveEntity = new PennyVerificationResponseEntity();
		try {
			saveEntity = pennyVerificationRepository.findByapplicationId(applicationId);
			//System.out.println("the saveEntity"+saveEntity.getAccountNo());
			BankEntity savedBankEntity = bankRepository.findByapplicationId(applicationId);
			//System.out.println("the savedBankEntity"+savedBankEntity.getAccountNo());
			if (saveEntity == null || !savedBankEntity.getAccountNo().trim().equals(saveEntity.getAccountNo().trim())) {
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(applicationId);
			if (savedBankEntity != null && user.isPresent()) {
				String reqBody = constructPennyDropRequestBody(savedBankEntity, user.get().getUserName());
				PennyVerificationResModel pennyRes = pennyDropDigioService.pennyUpdate(reqBody);
				if (pennyRes != null) {
					saveEntity = updatePennyVerificationEntity(applicationId, pennyRes, saveEntity);
					if ("true".equals(pennyRes.getFuzzyMatchResult()) && "true".equals(pennyRes.getVerified())) {
						saveEntity.setPennyConfirm(1);
						saveEntity.setAccountNo(savedBankEntity.getAccountNo());
						pennyVerificationRepository.save(saveEntity);
						responseModel.setReason(MessageConstants.PENNY_SUCCESS);
						responseModel.setResult(saveEntity);
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
						responseModel.setPage(EkycConstants.PAGE_SEGMENT);
					} else {
						saveEntity.setPennyConfirm(0);
						//saveEntity.setAccountNo(savedBankEntity.getAccountNo());
						pennyVerificationRepository.save(saveEntity);
						responseModel.setResult(MessageConstants.PENNY_DROP_NOT_PROCEED);
						responseModel.setMessage(EkycConstants.FAILED_MSG);
						responseModel.setStat(EkycConstants.FAILED_STATUS);
						responseModel.setResult(saveEntity);
					}
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
			} else {
				responseModel.setStat(EkycConstants.FAILED_STATUS);
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				responseModel.setReason(MessageConstants.PENNY_ALREADY_DONE);
				responseModel.setPage(EkycConstants.PAGE_SEGMENT);
			}
		} catch (Exception e) {
			handleException(responseModel, applicationId, e);
		}

		return responseModel;
	}

	private PennyVerificationResponseEntity updatePennyVerificationEntity(long applicationId,
			PennyVerificationResModel pennyRes, PennyVerificationResponseEntity saveEntity) {
		saveEntity = pennyVerificationRepository.findByapplicationId(applicationId);
		if (saveEntity == null) {
			saveEntity = new PennyVerificationResponseEntity();
			saveEntity.setApplicationId(applicationId);
		}
		saveEntity.setBeneficiaryNameWithBank(pennyRes.getBeneficiaryNameWithBank());
		saveEntity.setFuzzyMatchResult(pennyRes.getFuzzyMatchResult());
		saveEntity.setFuzzyMatchScore(pennyRes.getFuzzyMatchScore());
		saveEntity.setPaymentid(pennyRes.getId());
		saveEntity.setVerified(pennyRes.getVerified());
		saveEntity.setVerifiedAt(pennyRes.getVerifiedAt());
		pennyVerificationRepository.save(saveEntity);
		return saveEntity;
	}

	private String constructPennyDropRequestBody(BankEntity savedBankEntity, String userName) {
	    return String.format("{\n" +
	            "    \"beneficiary_account_no\": \"%s\",\n" +
	            "    \"beneficiary_ifsc\": \"%s\",\n" +
	            "    \"beneficiary_name\": \"%s\",\n" +
	            "    \"validation_mode\": \"PENNY_DROP\"\n" +
	            "}", savedBankEntity.getAccountNo(), savedBankEntity.getIfsc(), userName);
	}


	private void handleException(ResponseModel responseModel, long applicationId, Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.SaveLog(applicationId, "DigioPennyDropService", "createPennyDrop", e.getMessage());
		commonMethods.sendErrorMail(
				"An error occurred while processing your request. In createPennyDrop for the Error: " + e.getMessage(),
				"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
}

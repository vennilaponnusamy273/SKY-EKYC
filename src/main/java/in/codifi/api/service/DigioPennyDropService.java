package in.codifi.api.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		PennyVerificationResponseEntity savePennyEntity = new PennyVerificationResponseEntity();
		try {
			savePennyEntity = pennyVerificationRepository.findByapplicationId(applicationId);
			BankEntity savedBankEntity = bankRepository.findByapplicationId(applicationId);
			if (savePennyEntity == null || !savedBankEntity.getAccountNo().trim().equals(savePennyEntity.getAccountNo().trim())||savePennyEntity.getPennyConfirm()==0) {
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(applicationId);
			if (savedBankEntity != null && user.isPresent()) {
				String reqBody = constructPennyDropRequestBody(savedBankEntity);
				PennyVerificationResModel pennyRes = pennyDropDigioService.pennyUpdate(reqBody);
				ObjectMapper objectMapper = new ObjectMapper();
				String pennyResJson = objectMapper.writeValueAsString(pennyRes);
				System.out.println("the pennyResJson: " + pennyResJson);
				System.out.println("the 1 is penny ruuning");
				if (pennyRes != null) {
					savePennyEntity=updatePennyVerificationEntity(applicationId, pennyRes, savePennyEntity,savedBankEntity.getAccountNo());
					if(savePennyEntity.getPennyConfirm()==1) {
						System.out.println("the 4 is penny ruuning");
						responseModel.setReason(MessageConstants.PENNY_SUCCESS);
						responseModel.setResult(savePennyEntity);
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
						responseModel.setPage(EkycConstants.PAGE_SEGMENT);
					} else {
						responseModel.setReason(MessageConstants.PENNY_DROP_NOT_PROCEED);
						responseModel.setMessage(EkycConstants.FAILED_MSG);
						responseModel.setStat(EkycConstants.FAILED_STATUS);
						responseModel.setResult(savePennyEntity);
					}
					System.out.println("the 2 is penny ruuning");
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
		} catch (Exception ex) {
			 logger.error("An error occurred: " + ex.getMessage());
			 System.out.println("the 1error is penny ruuning");
		        ex.printStackTrace(); // Print the exception stack trace
		        commonMethods.SaveLog(applicationId, "DigioPennyDropService", "createPennyDrop", ex.getMessage());
		        commonMethods.sendErrorMail(
		                "An error occurred while processing your request. In createPennyDrop for the Error: " + ex.getMessage(),
		                "ERR-001");
		        responseModel = commonMethods.constructFailedMsg(ex.getMessage());
		}
		return responseModel;
	}

	private PennyVerificationResponseEntity updatePennyVerificationEntity(long applicationId,
			PennyVerificationResModel pennyRes, PennyVerificationResponseEntity saveEntity,String accNo) throws ParseException {
		saveEntity = pennyVerificationRepository.findByapplicationId(applicationId);
		if (saveEntity == null) {
			saveEntity = new PennyVerificationResponseEntity();
			saveEntity.setApplicationId(applicationId);
		}
		saveEntity.setAccountNo(accNo);
		saveEntity.setBeneficiaryNameWithBank(pennyRes.getBeneficiaryNameWithBank());
		//saveEntity.setFuzzyMatchResult(pennyRes.getFuzzyMatchResult());
		//saveEntity.setFuzzyMatchScore(pennyRes.getFuzzyMatchScore());
		saveEntity.setPaymentid(pennyRes.getId());
		// Ensure that pennyRes.getVerifiedAt() is not null
	    String verifiedAtString = pennyRes.getVerifiedAt();
	    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date originalDate = originalFormat.parse(verifiedAtString);

        // Format the Date object to a string using the desired format
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = newFormat.format(originalDate);

        // Print the formatted date for verification
        System.out.println("Formatted date: " + formattedDate);
		saveEntity.setVerified(pennyRes.getVerified());
		saveEntity.setVerifiedAt(formattedDate);
		
		if (pennyRes.getVerified()!=null&&pennyRes.getVerified().equals("true")) {
			saveEntity.setPennyConfirm(1);
		}
		else {
			saveEntity.setPennyConfirm(0);	
		}
		pennyVerificationRepository.save(saveEntity);
		return saveEntity;
	}

	private String constructPennyDropRequestBody(BankEntity savedBankEntity) {
	    return String.format("{\n" +
	            "    \"beneficiary_account_no\": \"%s\",\n" +
	            "    \"beneficiary_ifsc\": \"%s\",\n" +
	            "    \"validation_mode\": \"PENNY_DROP\"\n" +
	            "}", savedBankEntity.getAccountNo(), savedBankEntity.getIfsc());
	}
}

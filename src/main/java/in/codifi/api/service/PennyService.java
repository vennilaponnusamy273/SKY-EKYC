package in.codifi.api.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PennyDropEntity;
import in.codifi.api.helper.PennyDropHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.PennyDropRepository;
import in.codifi.api.service.spec.IPennyService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class PennyService implements IPennyService {
	@Inject
	CommonMethods commonMethods;
	@Inject
	PennyDropHelper pennyDropHelper;
	@Inject
	BankRepository bankRepository;
	@Inject
	PennyDropRepository pennyDropRepository;

	private static final Logger logger = LogManager.getLogger(PennyService.class);
	/**
	 * Method to Create Contact for penny drop
	 */
	@Override
	public ResponseModel createContact(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
		responseModel.setStat(EkycConstants.FAILED_STATUS);
		responseModel.setMessage(EkycConstants.FAILED_MSG);
		if (StringUtil.isNotNullOrEmpty(applicationUserEntity.getEmailId()) && applicationUserEntity.getMobileNo() != 0
				&& applicationUserEntity.getMobileNo() > 0) {
			PennyDropEntity oldDataEntity = pennyDropRepository.getPennyForContact(applicationUserEntity.getId(),
					applicationUserEntity.getEmailId(), Long.toString(applicationUserEntity.getMobileNo()),
					applicationUserEntity.getPanNumber());
			PennyDropEntity idNull = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
			if (oldDataEntity == null && idNull == null) {
				responseModel = pennyDropHelper.createContact(applicationUserEntity);
			} else {
				responseModel.setReason("already Present on this ID");
			}
		} else {
			if (StringUtil.isNullOrEmpty(applicationUserEntity.getEmailId())) {
				responseModel.setReason(MessageConstants.RZ_EMAIL_NULL);
			} else {
				responseModel.setReason(MessageConstants.RZ_MOBILE_NUMBER_NULL);
			}
		}
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.SaveLog(applicationUserEntity.getId(),"PennyService","createContact",e.getMessage());
		commonMethods.sendErrorMail("An error occurred while processing your request, In createContact for the Error: " + e.getMessage(),"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
	return responseModel;
}

	/**
	 * Method to add Account in created Contact
	 */
	@Override
	public ResponseModel addAccount(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
		responseModel.setStat(EkycConstants.FAILED_STATUS);
		responseModel.setMessage(EkycConstants.FAILED_MSG);
		BankEntity bankEntity = bankRepository.findByapplicationId(applicationUserEntity.getId());
		if (bankEntity != null && StringUtil.isNotNullOrEmpty(bankEntity.getAccountNo())
				&& StringUtil.isNotNullOrEmpty(bankEntity.getIfsc())) {
			PennyDropEntity pennyDropEntity = pennyDropRepository.getPennyForContact(applicationUserEntity.getId(),
					applicationUserEntity.getEmailId(), Long.toString(applicationUserEntity.getMobileNo()),
					applicationUserEntity.getPanNumber(), bankEntity.getAccountNo(), bankEntity.getIfsc());
			PennyDropEntity createdPenny = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
			if (pennyDropEntity == null && StringUtil.isNotNullOrEmpty(createdPenny.getRzContactId())) {
				responseModel = pennyDropHelper.addAccount(applicationUserEntity, createdPenny, bankEntity);
			} else {
				if (pennyDropEntity != null) {
					responseModel.setReason(MessageConstants.PENNY_ACCOUNT_CREATED);
				} else {
					responseModel.setReason(MessageConstants.PENNY_CONTACT_ID_NULL);
				}
			}
		} else {
			if (bankEntity == null) {
				responseModel.setReason(MessageConstants.BANK_DETAILS_NULL);
			} else if (StringUtil.isNotNullOrEmpty(bankEntity.getAccountNo())
					|| StringUtil.isNotNullOrEmpty(bankEntity.getIfsc())) {
				responseModel.setReason(MessageConstants.ACCOUNT_IFSC_NULL);
			}
		}
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.SaveLog(applicationUserEntity.getId(),"PennyService","addAccount",e.getMessage());
		commonMethods.sendErrorMail("An error occurred while processing your request, In addAccount for the Error: " + e.getMessage(),"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
	return responseModel;
}

	/**
	 * Method to put some penny Amount
	 */
	@Override
	public ResponseModel createPayout(ApplicationUserEntity applicationUserEntity, int confirmPenny) {
		ResponseModel responseModel = new ResponseModel();
		try {
		PennyDropEntity pennyDropEntity = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
		PennyDropEntity oldDataEntity = pennyDropRepository.getPennyForContact(applicationUserEntity.getId(),
				applicationUserEntity.getEmailId(), Long.toString(applicationUserEntity.getMobileNo()),
				applicationUserEntity.getPanNumber());
		if (oldDataEntity != null && oldDataEntity.getPennyAmount() == 0) {
			if (pennyDropEntity != null && StringUtil.isNotNullOrEmpty(pennyDropEntity.getRzFundAccountId())) {
				if (confirmPenny == 1) {
					responseModel = pennyDropHelper.createPayout(applicationUserEntity, pennyDropEntity);
				} else {
					pennyDropEntity.setConfirmPenny(confirmPenny);
					PennyDropEntity updatedEntity = pennyDropRepository.save(pennyDropEntity);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setReason(MessageConstants.PENNY_DROP_NOT_PROCEED);
					responseModel.setResult(updatedEntity);
				}
			} else {
				responseModel.setStat(EkycConstants.FAILED_STATUS);
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				if (pennyDropEntity == null) {
					responseModel.setReason(MessageConstants.PENNY_DETAILS_NULL);
				} else {
					responseModel.setReason(MessageConstants.FUND_ACCOUNT_ID_NULL);
				}
			}
		} else {
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			responseModel.setReason(MessageConstants.PENNY_ALREADY_DONE);
		}
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.SaveLog(applicationUserEntity.getId(),"PennyService","createPayout",e.getMessage());
		commonMethods.sendErrorMail("An error occurred while processing your request, In createPayout for the Error: " + e.getMessage(),"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
	return responseModel;
}

	/**
	 * Method to Validate Penny Details
	 */
	@Override
	public ResponseModel ValidateDetails(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseDTO = new ResponseModel();
		try {
		PennyDropEntity pennyDropEntity = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
		if (pennyDropEntity != null && StringUtil.isNullOrEmpty(pennyDropEntity.getAccountHolderName())) {
			responseDTO = pennyDropHelper.ValidateDetails(pennyDropEntity);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationUserEntity.getId(),"PennyService","ValidateDetails",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In ValidateDetails for the Error: " + e.getMessage(),"ERR-001");
			responseDTO = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseDTO;
	}

}

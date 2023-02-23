package in.codifi.api.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

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

@Service
public class PennyService implements IPennyService {
	@Inject
	CommonMethods commonMethods;
	@Inject
	PennyDropHelper pennyDropHelper;
	@Inject
	BankRepository bankRepository;
	@Inject
	PennyDropRepository pennyDropRepository;

	/**
	 * Method to Create Contact for penny drop
	 */
	@Override
	public ResponseModel createContact(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (StringUtil.isNotNullOrEmpty(applicationUserEntity.getEmailId()) && applicationUserEntity.getMobileNo() != 0
				&& applicationUserEntity.getMobileNo() > 0) {
			responseModel = pennyDropHelper.createContact(applicationUserEntity);
		} else {
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			if (StringUtil.isNullOrEmpty(applicationUserEntity.getEmailId())) {
				responseModel.setReason(MessageConstants.RZ_EMAIL_NULL);
			} else {
				responseModel.setReason(MessageConstants.RZ_MOBILE_NUMBER_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to add Account in created Contact
	 */
	@Override
	public ResponseModel addAccount(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = new ResponseModel();
		BankEntity bankEntity = bankRepository.findByapplicationId(applicationUserEntity.getId());
		PennyDropEntity pennyDropEntity = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
		if (bankEntity != null && pennyDropEntity != null && StringUtil.isNotNullOrEmpty(bankEntity.getAccountNo())
				&& StringUtil.isNotNullOrEmpty(bankEntity.getIfsc())
				&& StringUtil.isNotNullOrEmpty(pennyDropEntity.getRzContactId())) {
			responseModel = pennyDropHelper.addAccount(applicationUserEntity, pennyDropEntity, bankEntity);
		} else {
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			if (bankEntity == null) {
				responseModel.setReason(MessageConstants.BANK_DETAILS_NULL);
			} else if (pennyDropEntity == null) {
				responseModel.setReason(MessageConstants.PENNY_DETAILS_NULL);
			} else if (StringUtil.isNotNullOrEmpty(bankEntity.getAccountNo())
					|| StringUtil.isNotNullOrEmpty(bankEntity.getIfsc())) {
				responseModel.setReason(MessageConstants.ACCOUNT_IFSC_NULL);
			} else {
				responseModel.setReason(MessageConstants.PENNY_CONTACT_ID_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to put some penny Amount
	 */
	@Override
	public ResponseModel createPayout(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = new ResponseModel();
		PennyDropEntity pennyDropEntity = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
		if (pennyDropEntity != null && StringUtil.isNotNullOrEmpty(pennyDropEntity.getRzFundAccountId())) {
			responseModel = pennyDropHelper.createPayout(applicationUserEntity, pennyDropEntity);
		} else {
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			if (pennyDropEntity == null) {
				responseModel.setReason(MessageConstants.PENNY_DETAILS_NULL);
			} else {
				responseModel.setReason(MessageConstants.FUND_ACCOUNT_ID_NULL);
			}
		}
		return responseModel;
	}

	/**
	 * Method to Validate Penny Details
	 */
	@Override
	public ResponseModel ValidateDetails(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseDTO = new ResponseModel();
		PennyDropEntity pennyDropEntity = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
		if (pennyDropEntity != null) {
			responseDTO = pennyDropHelper.ValidateDetails(pennyDropEntity);
		}
		return responseDTO;
	}

}

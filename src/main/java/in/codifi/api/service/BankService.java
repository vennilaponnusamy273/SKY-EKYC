package in.codifi.api.service;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.model.BankAddressModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.service.spec.IBankService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Service
public class BankService implements IBankService {
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to save Bank Details
	 */
	@Override
	public ResponseModel saveBank(BankEntity bankEntity) {
		ResponseModel responseModel = new ResponseModel();
		BankEntity updatedEntity = null;
		Optional<ApplicationUserEntity> user = applicationUserRepository.findById(bankEntity.getApplicationId());
		if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0) {
			BankEntity savedBankEntity = bankRepository.findByapplicationId(bankEntity.getApplicationId());
			if (savedBankEntity != null) {
				bankEntity.setId(savedBankEntity.getId());
				updatedEntity = bankRepository.save(bankEntity);
			} else {
				updatedEntity = bankRepository.save(bankEntity);
			}
			if (updatedEntity != null && updatedEntity.getId() > 0) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(updatedEntity);
				responseModel.setPage(EkycConstants.PAGE_BANK);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_BANK_DETAILS);
			}
		} else {
			if (user.isEmpty()) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
			}
		}
		return responseModel;
	}

	/**
	 * Method to get Bank Details
	 */
	@Override
	public ResponseModel getBankByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		BankEntity savedBankEntity = bankRepository.findByapplicationId(applicationId);
		if (savedBankEntity != null) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedBankEntity);
			responseModel.setPage(EkycConstants.PAGE_PROFILE);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}

	/**
	 * Method to get Bank address by IFSC
	 */
	@Override
	public ResponseModel getBankAdd(String ifsc) {
		ResponseModel responseModel = new ResponseModel();
		BankAddressModel model = commonMethods.findBankAddressByIfsc(ifsc);
		if (model != null) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(model);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.IFSC_INVALID);
		}
		return responseModel;
	}

}

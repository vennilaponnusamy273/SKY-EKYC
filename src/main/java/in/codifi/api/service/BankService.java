package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import com.razorpay.Order;

import in.codifi.api.controller.spec.IPennyController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.helper.PaymentHelper;
import in.codifi.api.helper.RejectionStatusHelper;
import in.codifi.api.model.BankAddressModel;
import in.codifi.api.model.RazorpayModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.PaymentRepository;
import in.codifi.api.restservice.RazorpayIfscRestService;
import in.codifi.api.service.spec.IBankService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class BankService implements IBankService {
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	PaymentHelper paymentHelper;
	@Inject
	PaymentRepository paymentRepository;
	@Inject
	IPennyController iPennyController;
	@Inject
	RazorpayIfscRestService commonRestService;
	@Inject
	RejectionStatusHelper rejectionStatusHelper;
	
	private static final Logger logger = LogManager.getLogger(BankService.class);
	/**
	 * Method to save Bank Details
	 */
	@Override
	public ResponseModel saveBank(BankEntity bankEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
		BankEntity updatedEntity = null;
		Optional<ApplicationUserEntity> user = applicationUserRepository.findById(bankEntity.getApplicationId());
		if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0
				&& StringUtil.isEqual(bankEntity.getAccountNo(), bankEntity.getVerifyAccNumber())) {
			BankEntity savedBankEntity = bankRepository.findByapplicationId(bankEntity.getApplicationId());
			if (savedBankEntity != null) {
				bankEntity.setId(savedBankEntity.getId());
				updatedEntity = bankRepository.save(bankEntity);
			} else {
				updatedEntity = bankRepository.save(bankEntity);
			}
			rejectionStatusHelper.insertArchiveTableRecord(bankEntity.getApplicationId(), EkycConstants.PAGE_BANK);
			if (updatedEntity != null && updatedEntity.getId() > 0) {
				commonMethods.UpdateStep(EkycConstants.PAGE_BANK, bankEntity.getApplicationId());
				iPennyController.addAccount(bankEntity.getApplicationId());
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(updatedEntity);
				responseModel.setPage(EkycConstants.PAGE_SEGMENT);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_BANK_DETAILS);
			}
		} else {
			if (user.isEmpty()) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			} else if (StringUtil.isNotEqual(bankEntity.getAccountNo(), bankEntity.getVerifyAccNumber())) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ACC_NUM_MISMATCH);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
			}
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(bankEntity.getApplicationId(),"BankService","saveBank",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request. In SaveBankDetails for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to get Bank Details
	 */
	@Override
	public ResponseModel getBankByAppId(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		BankEntity savedBankEntity = bankRepository.findByapplicationId(applicationId);
		if (savedBankEntity != null) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedBankEntity);
			responseModel.setPage(EkycConstants.PAGE_BANK);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId,"BankService","getBankByAppId",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request. In getBankByAppId for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to get Bank address by IFSC
	 */
	@Override
	public ResponseModel getBankAdd(String ifsc) {
		ResponseModel responseModel = new ResponseModel();
		BankAddressModel model = null;
		try {
			model = commonRestService.getBankAddressByIfsc(ifsc);
		} catch (ClientWebApplicationException e) {
			//logger.error("An error occurred: " + e.getMessage());
			//commonMethods.SaveLog(null,"BankService","getBankAdd",e.getMessage());
			//commonMethods.sendErrorMail("An error occurred while processing your request. In getBankAdd for the Error: " + e.getMessage(), "ERR-001");
			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.IFSC_INVALID);
				return responseModel;
			} else {
				e.printStackTrace();
				logger.error("An error occurred: " + e.getMessage());
				commonMethods.SaveLog(null,"BankService","getBankAdd",e.getMessage());
				commonMethods.sendErrorMail("An error occurred while processing your request. In getBankAdd for the Error: " + e.getMessage(), "ERR-001");
				responseModel = commonMethods.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
				return responseModel;
			}
		}
		if (model != null) {
			
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(model);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.IFSC_INVALID);
		}
		return responseModel;
	}

	/**
	 * Method to create payment
	 */
	@Override
	public ResponseModel createPayment(PaymentEntity paymentEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
		RazorpayModel rzpayModel = paymentHelper.createPayment(paymentEntity);
		if (rzpayModel.getStat() == 1) {
			Order order = rzpayModel.getOrder();
			if (order != null) {
				PaymentEntity savedEntity = paymentHelper.populateRequiredFeilds(order, paymentEntity);
				if (savedEntity != null) {
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setResult(savedEntity);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVE_CREATE_PAYMENT);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PAYMENT_CREATION_FAILED);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.PAYMENT_CREATION_FAILED);
			responseModel.setReason(rzpayModel.getMessage());
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(paymentEntity.getApplicationId(),"BankService","createPayment",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request. In createPayment for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to verify payment
	 */
	@Override
	public ResponseModel verifyPayment(PaymentEntity params) {
		ResponseModel responseModel = new ResponseModel();
		try {
		PaymentEntity paymentEntity = paymentRepository.findByApplicationId(params.getApplicationId());
		if (paymentEntity != null) {
			boolean isEqual = paymentHelper.verifyPayment(params);
			if (isEqual) {
				PaymentEntity savedEntity = paymentHelper.saveVerifyPayment(params);
				if (savedEntity != null) {
					commonMethods.UpdateStep(EkycConstants.PAGE_PAYMENT, paymentEntity.getApplicationId());
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setPage(EkycConstants.PAGE_NOMINEE);
					responseModel.setResult(savedEntity);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVE_VERIFY_PAYMENT);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.VERIFY_NOT_SUCCEED);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.NOT_FOUND_DATA);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(params.getApplicationId(),"BankService","verifyPayment",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request. In verifyPayment for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to check payment
	 */
	@Override
	public ResponseModel checkPayment(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		PaymentEntity paymentDTO = paymentRepository.findByApplicationId(applicationId);
		if (paymentDTO != null) {
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(paymentDTO);
			if (StringUtil.isEqual(EkycConstants.RAZORPAY_STATUS_COMPLETED, paymentDTO.getStatus())) {
				responseModel.setMessage(MessageConstants.PAYMENT_ALREADY_COMPLETED);
			} else if (StringUtil.isEqual(EkycConstants.RAZORPAY_STATUS_CREATED, paymentDTO.getStatus())) {
				responseModel.setMessage(MessageConstants.PAYMENT_CREATED_COMPLETE_IT);
			}
		} else {
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(MessageConstants.PAYMENT_NOT_CREATED);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId,"BankService","checkPayment",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request. In checkPayment for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

}

package in.codifi.api.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IBankController;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.helper.PaymentHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IBankService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/bank")
public class BankController implements IBankController {
	@Inject
	IBankService bankService;
	@Inject
	CommonMethods commonMethods;
	@Inject
	PaymentHelper paymentHelper;

	/**
	 * Method to save Bank Details
	 */
	@Override
	public ResponseModel saveBank(BankEntity bankEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (bankEntity != null && bankEntity.getApplicationId() > 0) {
			responseModel = bankService.saveBank(bankEntity);
		} else {
			if (bankEntity != null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
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
		if (applicationId > 0) {
			responseModel = bankService.getBankByAppId(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to get Bank address by IFSC
	 */
	@Override
	public ResponseModel getBankAdd(String ifsc,long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (StringUtil.isNotNullOrEmptyAfterTrim(ifsc)) {
			responseModel = bankService.getBankAdd(ifsc);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to create payment
	 */
	@Override
	public ResponseModel createPayment(PaymentEntity paymentEntity) {
		ResponseModel responseModel = new ResponseModel();
		List<String> errorMsg = paymentHelper.validateCreatePayment(paymentEntity);
		if (StringUtil.isListNullOrEmpty(errorMsg)) {
			responseModel = bankService.createPayment(paymentEntity);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.INVLAID_PARAMETER);
			responseModel.setResult(errorMsg);
		}
		return responseModel;
	}

	/**
	 * Method to verify payment
	 */
	@Override
	public ResponseModel verifyPayment(PaymentEntity paymentEntity) {
		ResponseModel responseModel = new ResponseModel();
		List<String> errorMsg = paymentHelper.validateVerifyPayment(paymentEntity);
		if (StringUtil.isListNullOrEmpty(errorMsg)) {
			responseModel = bankService.verifyPayment(paymentEntity);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			responseModel.setResult(errorMsg);
		}
		return responseModel;
	}

	/**
	 * Method to check payment
	 */
	@Override
	public ResponseModel checkPayment(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = bankService.checkPayment(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}
}

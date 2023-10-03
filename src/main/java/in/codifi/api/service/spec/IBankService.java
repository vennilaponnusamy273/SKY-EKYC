package in.codifi.api.service.spec;

import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.model.ResponseModel;

public interface IBankService {

	/**
	 * Method to save Bank Details
	 * 
	 * @author prade
	 * @param bankEntity
	 * @return
	 */
	ResponseModel saveBank(BankEntity bankEntity);

	/**
	 * Method to get Bank Details
	 * 
	 * @author prade
	 * 
	 * @param applicationId
	 * @return
	 */
	ResponseModel getBankByAppId(long applicationId);

	/**
	 * Method to get Bank address by IFSC
	 * 
	 * @author prade
	 * 
	 * @param ifsc
	 * @return
	 */
	ResponseModel getBankAdd(String ifsc);

	/**
	 * Method to create payment
	 * 
	 * @author prade
	 * @param paymentEntity
	 * @return
	 */
	ResponseModel createPayment(PaymentEntity paymentEntity);

	/**
	 * Method to verify payment
	 * 
	 * @author prade
	 * @param paymentEntity
	 * @return
	 */
	ResponseModel verifyPayment(PaymentEntity paymentEntity);

	/**
	 * Method to check payment
	 * 
	 * @author prade
	 * @param paymentEntity
	 * @return
	 */
	ResponseModel checkPayment(long applicationId);
}

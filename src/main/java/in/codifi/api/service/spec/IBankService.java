package in.codifi.api.service.spec;

import in.codifi.api.entity.BankEntity;
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
}

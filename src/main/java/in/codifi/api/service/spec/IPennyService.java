package in.codifi.api.service.spec;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;

public interface IPennyService {
	/**
	 * Method to Create Contact for penny drop
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	ResponseModel createContact(ApplicationUserEntity applicationUserEntity);

	/**
	 * Method to add Account in created Contact
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	ResponseModel addAccount(ApplicationUserEntity applicationUserEntity);

	/**
	 * Method to put some penny Amount
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	ResponseModel createPayout(ApplicationUserEntity applicationUserEntity, int confirmPenny);

	/**
	 * Method to Validate Penny Details
	 * 
	 * @author prade
	 * @param application id
	 * @return
	 */
	ResponseModel ValidateDetails(ApplicationUserEntity applicationUserEntity);

}

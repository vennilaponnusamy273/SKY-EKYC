package in.codifi.api.service.spec;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;

public interface IPanService {
	/**
	 * Method to get pan details from NSDL
	 * 
	 * @author Vennila Ponnusamy
	 * @param PanCardDetails
	 * @return
	 */

	ResponseModel getPanDetails(ApplicationUserEntity pan);

	/**
	 * Method to save Date of birth
	 * 
	 * @author prade
	 * @param PanCardDetails
	 * @return
	 */

	ResponseModel saveDob(ApplicationUserEntity userEntity);

	/**
	 * Method to Confirm KRA Address
	 * 
	 * @author prade
	 * @param pan
	 * @return
	 */
	ResponseModel confirmAddress(long applicationId);

	/**
	 * Method to Confirm Pan Details
	 * 
	 * @author prade
	 * @param pan
	 * @return
	 */
	ResponseModel confirmPan(long applicationId);

}

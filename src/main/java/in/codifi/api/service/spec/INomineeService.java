package in.codifi.api.service.spec;

import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.NomineeDocModel;
import in.codifi.api.model.ResponseModel;

public interface INomineeService {
	/**
	 * Method to get Nominee Details
	 * 
	 * @author prade
	 * 
	 * @param applicationId
	 * @return
	 */
	ResponseModel getNominee(long applicationId);

	/**
	 * Method to save and upload Nominee Proof
	 * 
	 * @author prade
	 * 
	 * @param fileModel
	 * @return
	 */

	ResponseModel uploadDocNominee(NomineeDocModel fileModel);

	/**
	 * Method to delete Nominee
	 * 
	 * @author prade
	 * 
	 * @param id
	 * @return
	 */
	ResponseModel deleteNom(long id);

	/**
	 * Method to update Nominee Alloction
	 * 
	 * @author prade
	 * @param paymentEntity
	 * @return
	 */
	ResponseModel updateNomineeAllocation(NomineeEntity nomineeEntity);

}
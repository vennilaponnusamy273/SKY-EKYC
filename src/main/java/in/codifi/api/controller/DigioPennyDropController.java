package in.codifi.api.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IDigioPennyDropController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.service.spec.IDigioPennyDropService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/digioPenny")
public class DigioPennyDropController implements IDigioPennyDropController {

	@Inject
	ApplicationUserRepository userRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	IDigioPennyDropService iDigioPennyDropService;
	@Override
	public ResponseModel createPenny(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				responseModel = iDigioPennyDropService.createPennyDrop(applicationId);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

}

package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.service.spec.IDigilockerService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class DigilockerService implements IDigilockerService {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;

	private static final Logger logger = LogManager.getLogger(DigilockerService.class);
	/**
	 * Method to intialize digi locker
	 */
	@Override
	public ResponseModel iniDigilocker(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
		if (isUserPresent.isPresent()) {
			String redirectUrl = props.getDigiBaseUrl() + EkycConstants.DIGI_CONST_AUTH_CLIENT_ID
					+ props.getDigiClientId() + EkycConstants.DIGI_CONST_RES_TYPE + props.getDigiResponseCode()
					+ EkycConstants.DIGI_CONST_STATE + applicationId;
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(redirectUrl);
			responseModel.setPage(EkycConstants.PAGE_AADHAR);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.sendErrorMail("An error occurred while processing your request, In iniDigilocker for the Error: " + e.getMessage(),"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
		return responseModel;
	}
}

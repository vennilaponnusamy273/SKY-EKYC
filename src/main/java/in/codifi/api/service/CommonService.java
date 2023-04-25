package in.codifi.api.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.AccesslogEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.service.spec.ICommonService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class CommonService implements ICommonService {
	@Inject
	AccesslogRepository logRepository;
	@Inject
	CommonMethods commonMethods;

	private static final Logger logger = LogManager.getLogger(CommonService.class);
	/**
	 * Method to get log details
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	public ResponseModel getLogDetails() {
		ResponseModel response = new ResponseModel();
		try {
		Iterable<AccesslogEntity> getLogDeatils = logRepository.findAll();
		if (getLogDeatils != null) {
			response.setStat(EkycConstants.SUCCESS_STATUS);
			response.setMessage(EkycConstants.SUCCESS_MSG);
			response.setResult(getLogDeatils);
		} else {
			response.setStat(EkycConstants.FAILED_STATUS);
			response.setMessage(EkycConstants.FAILED_MSG);
			response.setResult(getLogDeatils);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In getLogDetails for the Error: " + e.getMessage(),"ERR-001");
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}
}

package in.codifi.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.AccesslogEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.service.spec.ICommonService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class CommonService implements ICommonService {
	@Inject
	AccesslogRepository logRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	KraKeyValueRepository kraKeyValueRepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;

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
			commonMethods.SaveLog(null, "CommonService", "getLogDetails", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request, In getLogDetails for the Error: "
							+ e.getMessage(), "ERR-001");
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseModel pageJumb(String pagesnumber) {
		ResponseModel response = new ResponseModel();
		try {
			String keyValue = kraKeyValueRepository.getkeyValueForKra("11", "STAGE", pagesnumber);
			if (keyValue != null) {
				int i = 0;
				Map<Integer, String> pageMappings = new HashMap<>();
				pageMappings.put(i++, "0.5");
				pageMappings.put(i++, "1");
				pageMappings.put(i++, "1.1");
				pageMappings.put(i++, "2");
				pageMappings.put(i++, "2.1");
				pageMappings.put(i++, "2.2");
				pageMappings.put(i++, "2.3");
				pageMappings.put(i++, "3");
				pageMappings.put(i++, "4");
				pageMappings.put(i++, "5");
				pageMappings.put(i++, "5.1");
				pageMappings.put(i++, "6");
				pageMappings.put(i++, "7");
				pageMappings.put(i++, "8");
				pageMappings.put(i++, "8.1");
				pageMappings.put(i++, "8.2");
				pageMappings.put(i++, "8.3");
				pageMappings.put(i++, "9");
				pageMappings.put(i++, "10");
				pageMappings.put(i++, "11");
				pageMappings.put(i++, "12");
				pageMappings.put(i++, "13");
				Random rand = new Random();
				int n = rand.nextInt(21);
				response.setPage(pageMappings.get(n + 1));
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.KEYVALUE_NOTFOUND);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "CommonService", "pageJumb", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In pageJumb for the Error: " + e.getMessage(),
					"ERR-001");
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

	/**
	 * Method to update nominee opted out
	 */
	@Override
	public ResponseModel updateNomineeOptedOut(long applicationId) {
		ResponseModel response = new ResponseModel();
		int updateCount = applicationUserRepository.updateNomineeOptedOut(applicationId, 1);
		if (updateCount > 0) {
			response.setStat(EkycConstants.SUCCESS_STATUS);
			response.setMessage(EkycConstants.SUCCESS_MSG);
			response.setReason(MessageConstants.NOMINEE_OPTED_OUT);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.NOMINEE_OPTED_OUT_FAILED);
		}
		return response;
	}
}

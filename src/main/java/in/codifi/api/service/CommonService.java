package in.codifi.api.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.entity.AccesslogEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.service.spec.ICommonService;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class CommonService implements ICommonService {
	@Inject
	AccesslogRepository logRepository;

	/**
	 * Method to get log details
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	public ResponseModel getLogDetails() {
		ResponseModel response = new ResponseModel();
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
		return response;
	}
}

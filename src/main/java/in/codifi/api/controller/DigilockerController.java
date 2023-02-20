package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.controller.spec.IDigilockerController;
import in.codifi.api.helper.DigilockerHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IDigilockerService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/digi")
public class DigilockerController implements IDigilockerController {
	@Inject
	DigilockerHelper digilockerHelper;
	@Inject
	CommonMethods commonMethods;
	@Inject
	IDigilockerService service;

	/**
	 * Method to intialize digilocker
	 * 
	 * @return
	 */
	@Override
	public ResponseModel iniDigilocker(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = service.iniDigilocker(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to save address from digi
	 */
	@Override
	public ResponseModel saveDigi(String code, String state, long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try
		{
		if (StringUtil.isNotNullOrEmpty(code) && StringUtil.isNotNullOrEmpty(state) && applicationId > 0) {
			responseModel = digilockerHelper.saveDigi(code, state, applicationId);
		} else {
			if (StringUtil.isNullOrEmpty(code)) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.DIGI_CODE_NULL);
			} else if (applicationId <= 0) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.DIGI_STATE_NULL);
			}
		}
		String Request_tble="code:"+code+"state:"+state+"applicationId:"+applicationId;
		ObjectMapper mapper = new ObjectMapper();
		String Res = mapper.writeValueAsString(responseModel);
		commonMethods.saveRequestAndResposne(Request_tble,Res,EkycConstants.DIGI,applicationId);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
}
}

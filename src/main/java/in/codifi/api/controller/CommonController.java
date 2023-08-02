package in.codifi.api.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.DataLoader;
import in.codifi.api.controller.spec.ICommonController;
import in.codifi.api.model.AddressModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.ICommonService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/common")
public class CommonController implements ICommonController {

	@Inject
	DataLoader dataLoader;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ICommonService commonService;

	/**
	 * Method to reload KRA hazle cache
	 */
	@Override
	public String reloadKraKeyValue() {
		if (HazleCacheController.getInstance().getKraKeyValue() != null) {
			dataLoader.reloadHazleCache();
		}
		return MessageConstants.STATUS_OK;
	}

	/**
	 * Method to get address by pincode
	 */
	@Override
	public ResponseModel getAddress(String pincode,long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		List<AddressModel> model = commonMethods.findAddressByPinCode(pincode);
		if (StringUtil.isListNotNullOrEmpty(model)) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(model);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.PINCODE_INVALID);
		}
		return responseModel;
	}

	/**
	 * Method to get log details
	 */
	public ResponseModel getLogDetails(long applicationId) {
		return commonService.getLogDetails();
	}

	/**
	 * Method to update Nominee OptedOut
	 */
	@Override
	public ResponseModel updateNomineeOptedOut(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = commonService.updateNomineeOptedOut(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	@Override
	public ResponseModel pageJumb(String pagesnumber,long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (pagesnumber != null) {
			responseModel = commonService.pageJumb(pagesnumber);
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
}

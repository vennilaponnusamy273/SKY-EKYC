package in.codifi.api.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.DataLoader;
import in.codifi.api.controller.spec.ICommonController;
import in.codifi.api.model.AddressModel;
import in.codifi.api.model.ResponseModel;
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

	/**
	 * Method to reload KRA hazle cache
	 */
	@Override
	public String reloadKraKeyValue() {
		if (HazleCacheController.getInstance().getKraKeyValue() != null) {
			HazleCacheController.getInstance().getKraKeyValue().clear();
			dataLoader.reloadHazleCache();
		}
		return MessageConstants.STATUS_OK;
	}

	/**
	 * Method to get address by pincode
	 */
	@Override
	public ResponseModel getAddress(String pincode) {
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
}

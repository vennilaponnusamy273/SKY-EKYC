package in.codifi.api.controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IPanController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IPanService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/pan")
public class PanController implements IPanController {
	@Inject
	IPanService service;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to save pan id to get details
	 */
	@Override
	public ResponseModel getPanDetails(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getPanNumber()) && userEntity.getId() > 0) {
			responseModel = service.getPanDetails(userEntity);
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				if (userEntity.getId() <= 0) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.PAN_NUMBER_NULL);
				}
			}
		}
		commonMethods.Req_Res_Save_object(userEntity, responseModel, EkycConstants.PAN, userEntity.getId());
		return responseModel;
	}

	/**
	 * Method to save Date Of Birth
	 */
	@Override
	public ResponseModel saveDob(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (userEntity != null && StringUtil.isNotNullOrEmpty(userEntity.getDob()) && userEntity.getId() > 0) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate localDate = LocalDate.parse(userEntity.getDob(), formatter);
			LocalDate today = LocalDate.now();
			Period p = Period.between(localDate, today);
			if (p.getYears() > 18) {
				responseModel = service.saveDob(userEntity);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.AGE_RESTRICTION);
			}
		} else {
			if (userEntity == null) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else {
				if (userEntity.getId() <= 0) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.PAN_NUMBER_NULL);
				}
			}
		}
		commonMethods.Req_Res_Save_object(userEntity, responseModel, EkycConstants.PAN_DOB, userEntity.getId());
		return responseModel;
	}
}

package in.codifi.api.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.INomineeController;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.PaymentRepository;
import in.codifi.api.service.spec.INomineeService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Path("/nominee")
public class NomineeController implements INomineeController {
	@Inject
	INomineeService service;
	@Inject
	CommonMethods commonMethods;
	@Inject
	PaymentRepository paymentRepository;

	/**
	 * Method to save nominee and Guardian Details
	 */
	@Override
	public ResponseModel saveNominee(List<NomineeEntity> nomineeEntity) {
		ResponseModel responseModel = new ResponseModel();
		if (StringUtil.isListNotNullOrEmpty(nomineeEntity)) {
			PaymentEntity paymentDTO = paymentRepository.findByApplicationId(nomineeEntity.get(0).getApplicationId());
			if (paymentDTO != null
					&& StringUtil.isEqual(EkycConstants.RAZORPAY_STATUS_COMPLETED, paymentDTO.getStatus())) {
				responseModel = service.saveNominee(nomineeEntity);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.COMPLETE_PAYMENT_FIRST);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to get nominee and Guardian Details
	 */
	@Override
	public ResponseModel getNominee(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = service.getNominee(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

}

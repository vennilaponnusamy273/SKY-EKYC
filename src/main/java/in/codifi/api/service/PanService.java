package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.json.JSONObject;

import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.helper.KRAHelper;
import in.codifi.api.helper.PanHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.service.spec.IPanService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class PanService implements IPanService {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	PanHelper panHelper;
	@Inject
	CommonMethods commonMethods;
	@Inject
	KRAHelper kraHelper;
	@Inject
	AddressRepository addressRepository;
	@Inject
	CkycService ckycService;

	/**
	 * Method to get PAN details
	 */
	@Override
	public ResponseModel getPanDetails(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
			ApplicationUserEntity panNumberPresent = repository.findByPanNumber(userEntity.getPanNumber());
			if (isUserPresent.isPresent() && (panNumberPresent == null
					|| panNumberPresent != null && userEntity.getId() == panNumberPresent.getId())) {
				String result = panHelper.getPanDetailsFromNSDL(userEntity.getPanNumber(), userEntity.getId());
				if (result != null && !result.equalsIgnoreCase("")) {
					responseModel = panHelper.saveResult(result, isUserPresent.get());
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_PAN_MSG);
				}
			} else {
				if (!isUserPresent.isPresent()) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.PAN_ALREADY_AVAILABLE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to save Date of Birth
	 */
	@Override
	public ResponseModel saveDob(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		ProfileEntity profileEntity = null;
		Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
		if (isUserPresent.isPresent()) {
			ApplicationUserEntity oldUserEntity = isUserPresent.get();
			oldUserEntity.setDob(userEntity.getDob());
			ApplicationUserEntity savingEntity = repository.save(oldUserEntity);
			commonMethods.UpdateStep(EkycConstants.PAGE_PAN_CONFIRM, userEntity.getId());
			try {
				if (StringUtil.isNotNullOrEmpty(savingEntity.getPanNumber())
						&& StringUtil.isNotNullOrEmpty(savingEntity.getDob())) {
					JSONObject pancardResponse = kraHelper.getPanCardStatus(savingEntity.getPanNumber());
					if (pancardResponse != null) {
						if (pancardResponse.has("APP_NAME")) {
							int panCardStatus = pancardResponse.getInt("APP_STATUS");
							if (checkAppStatus(panCardStatus)) {
								JSONObject panCardDetails = kraHelper.getPanCardDetails(savingEntity.getPanNumber(),
										savingEntity.getDob(), panCardStatus);
								if (panCardDetails != null) {
									if (panCardDetails.has("APP_NAME")) {
										profileEntity = kraHelper.updateDetailsFromKRA(panCardDetails,
												userEntity.getId());
									} else {
										if (panCardDetails.has("ERROR_MSG")) {
											responseModel = commonMethods
													.constructFailedMsg(pancardResponse.getString("ERROR_MSG"));
											responseModel.setPage(EkycConstants.PAGE_AADHAR);
										} else {
											responseModel = commonMethods
													.constructFailedMsg(MessageConstants.KRA_FAILED);
											responseModel.setPage(EkycConstants.PAGE_AADHAR);
										}
									}
								} else {
									responseModel = commonMethods
											.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
									responseModel.setPage(EkycConstants.PAGE_AADHAR);
								}
								ckycService.saveCkycResponse(userEntity.getId());
							}
						} else {
							if (pancardResponse.has("ERROR_MSG")) {
								responseModel = commonMethods
										.constructFailedMsg(pancardResponse.getString("ERROR_MSG"));
								responseModel.setPage(EkycConstants.PAGE_AADHAR);
							} else {
								responseModel = commonMethods.constructFailedMsg(MessageConstants.KRA_FAILED);
								responseModel.setPage(EkycConstants.PAGE_AADHAR);
							}
						}
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
						responseModel.setPage(EkycConstants.PAGE_AADHAR);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				responseModel = commonMethods.constructFailedMsg(e.getMessage());
			}
			if (profileEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(profileEntity);
				responseModel.setPage(EkycConstants.PAGE_AADHAR);
			} else if (savingEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(savingEntity);
				responseModel.setPage(EkycConstants.PAGE_AADHAR);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_DOB);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}

		return responseModel;
	}

	public boolean checkAppStatus(int appStatuscode) {
		boolean isPresent = false;
		if (appStatuscode == 2 || appStatuscode == 002 || appStatuscode == 102 || appStatuscode == 202
				|| appStatuscode == 302 || appStatuscode == 402 || appStatuscode == 502 || appStatuscode == 7
				|| appStatuscode == 007 || appStatuscode == 107 || appStatuscode == 207 || appStatuscode == 307
				|| appStatuscode == 407 || appStatuscode == 507) {
			isPresent = true;
		}
		return isPresent;
	}

	/**
	 * Method to Confirm KRA Address
	 */
	@Override
	public ResponseModel confirmAddress(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
		if (isUserPresent.isPresent()) {
			AddressEntity savedEntity = addressRepository.findByapplicationId(applicationId);
			if (savedEntity != null) {
				savedEntity.setAddressConfirm(1);
				addressRepository.save(savedEntity);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(savedEntity);
				responseModel.setPage(EkycConstants.PAGE_PROFILE);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ADDRESS_NOT_YET);
			}
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}

	/**
	 * Method to Confirm Pan Details
	 */
	@Override
	public ResponseModel confirmPan(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
		if (isUserPresent.isPresent()) {
			ApplicationUserEntity savedUserEntity = isUserPresent.get();
			savedUserEntity.setPanConfirm(1);
			repository.save(savedUserEntity);
			commonMethods.UpdateStep(EkycConstants.PAGE_PAN_CONFIRM, applicationId);
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedUserEntity);
			responseModel.setPage(EkycConstants.PAGE_PAN_KRA_DOB_ENTRY);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}
}

package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.helper.KRAHelper;
import in.codifi.api.helper.PanHelper;
import in.codifi.api.helper.RejectionStatusHelper;
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
	@Inject
	RejectionStatusHelper rejectionStatusHelper;

	private static final Logger logger = LogManager.getLogger(PanService.class);

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
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "PanService", "getPanDetails", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request, In getPanDetails for this Error :"
							+ e.getMessage(), "ERR-001");
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
		try {
			ProfileEntity profileEntity = null;
			ApplicationUserEntity savingEntity = null;
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
			if (isUserPresent.isPresent()) {
				ApplicationUserEntity oldUserEntity = isUserPresent.get();
				oldUserEntity.setDob(userEntity.getDob());
				try {
					if (StringUtil.isNotNullOrEmpty(oldUserEntity.getPanNumber())
							&& StringUtil.isNotNullOrEmpty(oldUserEntity.getDob())) {
						JSONObject pancardResponse = kraHelper.getPanCardStatus(oldUserEntity.getPanNumber(),
								userEntity.getId());
						if (pancardResponse != null) {
							if (pancardResponse.has("APP_NAME")) {
								int panCardStatus = pancardResponse.getInt("APP_STATUS");
								oldUserEntity.setPanStatusCode(Integer.toString(panCardStatus));
								if (checkAppStatus(panCardStatus)) {
									JSONObject panCardDetails = kraHelper.getPanCardDetails(
											oldUserEntity.getPanNumber(), userEntity.getDob(), panCardStatus,
											userEntity.getId());
									if (panCardDetails != null) {
										
										if (panCardDetails.has("APP_NAME")
												|| (panCardDetails.has(EkycConstants.CONSTANT_ERROR_DESC)
														&& StringUtil.isEqual(
																panCardDetails
																		.getString(EkycConstants.CONSTANT_ERROR_DESC),
																MessageConstants.ERROR_MSG_INVALID_PAN))) {
											savingEntity = repository.save(oldUserEntity);
										 
										if (panCardDetails.has("APP_NAME")) {
											profileEntity = kraHelper.updateDetailsFromKRA(panCardDetails,
													userEntity.getId(), panCardStatus);
											// ckycService.saveCkycResponse(userEntity.getId());
										}
										 } 
										else {
											if (panCardDetails.has(EkycConstants.CONSTANT_ERROR_MSG)) {
												responseModel = commonMethods.constructFailedMsg(
														panCardDetails.getString(EkycConstants.CONSTANT_ERROR_MSG));
												responseModel.setPage(EkycConstants.PAGE_AADHAR);
												savingEntity = repository.save(oldUserEntity);
												return responseModel;
											} else if (panCardDetails.has(EkycConstants.CONSTANT_ERROR_DESC)) {
												responseModel = commonMethods.constructFailedMsg(
														panCardDetails.getString(EkycConstants.CONSTANT_ERROR_DESC));
												responseModel.setPage(EkycConstants.PAGE_AADHAR);
												savingEntity = repository.save(oldUserEntity);
												return responseModel;
											} else {
												responseModel = commonMethods
														.constructFailedMsg(MessageConstants.KRA_FAILED);
												responseModel.setPage(EkycConstants.PAGE_AADHAR);
												savingEntity = repository.save(oldUserEntity);
												return responseModel;
											}
										}
									} else {
										responseModel = commonMethods
												.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
									}
								} else {
									// ckycService.saveCkycResponse(userEntity.getId());
									savingEntity = repository.save(oldUserEntity);
									// ckycService.saveCkycResponse(userEntity.getId());
								}
							} else {
								if (pancardResponse.has(EkycConstants.CONSTANT_ERROR_MSG)) {
									responseModel = commonMethods.constructFailedMsg(
											pancardResponse.getString(EkycConstants.CONSTANT_ERROR_MSG));
								} else {
									responseModel = commonMethods.constructFailedMsg(MessageConstants.KRA_FAILED);
								}
							}
						} else {
							responseModel = commonMethods.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
						}
					}
					responseModel.setPage(EkycConstants.PAGE_AADHAR);
					responseModel.setResult(
							savingEntity != null ? savingEntity : profileEntity != null ? profileEntity : "");
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("An error occurred: " + e.getMessage());
					commonMethods.SaveLog(userEntity.getId(), "PanService", "saveDob", e.getMessage());
					commonMethods
							.sendErrorMail("An error occurred while processing your request, In saveDob for the Error: "
									+ e.getMessage(), "ERR-001");
					responseModel = commonMethods.constructFailedMsg(e.getMessage());
				}
				if (StringUtil.isNullOrEmpty(responseModel.getMessage())) {
					if (profileEntity != null || savingEntity != null) {
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_DOB);
					}
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
			commonMethods.UpdateStep(EkycConstants.PAGE_PAN_KRA_DOB_ENTRY, userEntity.getId());
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "PanService", "saveDob", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In saveDob for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
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
		try {
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				AddressEntity savedEntity = addressRepository.findByapplicationId(applicationId);
				if (savedEntity != null) {
					savedEntity.setAddressConfirm(1);
					addressRepository.save(savedEntity);
					commonMethods.UpdateStep(EkycConstants.PAGE_AADHAR, applicationId);
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
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "PanService", "confirmAddress", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request, In confirmAddress for the Error: "
							+ e.getMessage(), "ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to Confirm Pan Details
	 */
	@Override
	public ResponseModel confirmPan(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
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
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "PanService", "confirmPan", e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In confirmPan.", "ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
}

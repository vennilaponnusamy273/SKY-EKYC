package in.codifi.api.service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.helper.PanHelper;
import in.codifi.api.helper.UserHelper;
import in.codifi.api.model.ErpExistingApiModel;
import in.codifi.api.model.ExistingCustReqModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.restservice.ErpRestService;
import in.codifi.api.service.spec.IUserService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Service
public class UserService implements IUserService {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	UserHelper userHelper;
	@Inject
	ErpRestService erpRestService;
	@Inject
	PanHelper panHelper;
	@Inject
	ApplicationProperties props;

	/**
	 * Method to send otp to mobile number
	 */
	public ResponseModel sendSmsOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ApplicationUserEntity updatedUserDetails = null;
			ApplicationUserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			int otp = commonMethods.generateOTP(userEntity.getMobileNo());
			String mapKey = String.valueOf(userEntity.getMobileNo()) + EkycConstants.SMS_KEY;
			if (oldUserEntity == null) {
				// send OTP
				updatedUserDetails = userHelper.saveOrUpdateUser(otp, userEntity);
			} else {
				// resend OTP
				if (HazleCacheController.getInstance().getRetryOtp().containsKey(mapKey)) {
					if (HazleCacheController.getInstance().getRetryOtp().get(mapKey) > 4) {
						long expiryTime = HazleCacheController.getInstance().getRetryOtp().getEntryView(mapKey)
								.getExpirationTime();
						Date expiry = new Date((expiryTime));
						return commonMethods.constructFailedMsg(MessageConstants.RETRY_OTP_TRY_AFTER + expiry);
					} else {
						if (HazleCacheController.getInstance().getRetryOtp().get(mapKey) == 4) {
							updatedUserDetails = userHelper.saveOrUpdateUser(otp, oldUserEntity);
							HazleCacheController.getInstance().getRetryOtp().put(mapKey,
									HazleCacheController.getInstance().getRetryOtp().get(mapKey) + 1, 300000,
									TimeUnit.MILLISECONDS);
						} else {
							HazleCacheController.getInstance().getRetryOtp().put(mapKey,
									HazleCacheController.getInstance().getRetryOtp().get(mapKey) + 1);
							updatedUserDetails = userHelper.saveOrUpdateUser(otp, oldUserEntity);
						}
					}
				} else {
					HazleCacheController.getInstance().getRetryOtp().put(mapKey, 1);
					updatedUserDetails = userHelper.saveOrUpdateUser(otp, oldUserEntity);
				}
			}
			if (updatedUserDetails != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(updatedUserDetails);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_GENERATE_OTP);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to validate Sms OTP
	 */
	@Override
	public ResponseModel verifySmsOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = null;
		try {
			ApplicationUserEntity updatedUserDetails = null;
			ApplicationUserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			if (oldUserEntity != null) {
				String mapKey = String.valueOf(oldUserEntity.getMobileNo()) + EkycConstants.SMS_KEY;
				if (HazleCacheController.getInstance().getVerifyOtp().containsKey(mapKey)) {
					int savedOtp = HazleCacheController.getInstance().getVerifyOtp().get(mapKey);
					if (savedOtp == userEntity.getSmsOtp()) {
						oldUserEntity.setSmsVerified(1);
						updatedUserDetails = repository.save(oldUserEntity);
						ExistingCustReqModel custModel = new ExistingCustReqModel();
						custModel.setInput(String.valueOf(oldUserEntity.getMobileNo()));
						custModel.setInputType(EkycConstants.ERP_MOBILE);
						ErpExistingApiModel existingModel = erpRestService.erpCheckExisting(custModel);
						HazleCacheController.getInstance().getVerifyOtp().remove(mapKey);
						HazleCacheController.getInstance().getFailedOtp().remove(mapKey);
						HazleCacheController.getInstance().getRetryOtp().remove(mapKey);
						if (updatedUserDetails != null && StringUtil.isNotNullOrEmpty(updatedUserDetails.getStatus())
								&& StringUtil.isEqual(updatedUserDetails.getStatus(),
										EkycConstants.EKYC_STATUS_INPROGRESS)) {
							responseModel = new ResponseModel();
							responseModel.setMessage(EkycConstants.SUCCESS_MSG);
							responseModel.setStat(EkycConstants.SUCCESS_STATUS);
							responseModel.setResult(updatedUserDetails);
							// corresponding page
						} else {
							if (updatedUserDetails == null) {
								responseModel = commonMethods
										.constructFailedMsg(MessageConstants.ERROR_WHILE_VERIFY_OTP);
							} else {
								if (existingModel != null && StringUtil.isNotNullOrEmpty(existingModel.getExisting())
										&& StringUtil.isEqual(existingModel.getExisting(), EkycConstants.EXISTING_YES)
										&& StringUtil.isNotNullOrEmpty(existingModel.getStatus())
										&& StringUtil.isNotEqual(existingModel.getExisting(),
												EkycConstants.STATUS_INACTIVE)) {
									if (StringUtil.isNotNullOrEmpty(existingModel.getStatus()) && StringUtil
											.isEqual(existingModel.getExisting(), EkycConstants.STATUS_ACTIVE)) {
										responseModel = commonMethods
												.constructFailedMsg(MessageConstants.EKYC_ACTIVE_CUSTOMER);
									} else if (StringUtil.isNotNullOrEmpty(existingModel.getStatus()) && StringUtil
											.isEqual(existingModel.getExisting(), EkycConstants.STATUS_DORMANT)) {
										responseModel = commonMethods
												.constructFailedMsg(MessageConstants.EKYC_DORMANT_CUSTOMER);
										responseModel.setPage(EkycConstants.PAGE_PDFDOWNLOAD);
									}
								} else {
									responseModel = new ResponseModel();
									responseModel.setMessage(EkycConstants.SUCCESS_MSG);
									responseModel.setStat(EkycConstants.SUCCESS_STATUS);
									responseModel.setResult(updatedUserDetails);
									responseModel.setPage(EkycConstants.PAGE_EMAIL);
								}
							}
						}
					} else {
						if (HazleCacheController.getInstance().getFailedOtp().containsKey(mapKey)) {
							if (HazleCacheController.getInstance().getFailedOtp().get(mapKey) > 3) {
								long expiryTime = HazleCacheController.getInstance().getFailedOtp().getEntryView(mapKey)
										.getExpirationTime();
								Date expiry = new Date((expiryTime));
								return commonMethods
										.constructFailedMsg(MessageConstants.INVALID_OTP_TRY_AFTER + expiry);
							} else {
								if (HazleCacheController.getInstance().getFailedOtp().get(mapKey) == 2) {
									HazleCacheController.getInstance().getFailedOtp().put(mapKey,
											HazleCacheController.getInstance().getFailedOtp().get(mapKey) + 1, 300000,
											TimeUnit.MILLISECONDS);
								} else {
									HazleCacheController.getInstance().getFailedOtp().put(mapKey,
											HazleCacheController.getInstance().getFailedOtp().get(mapKey) + 1);
								}
								responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
							}
						} else {
							HazleCacheController.getInstance().getFailedOtp().put(mapKey, 1);
							responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
						}
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.OTP_TIME_EXPIRED);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_WRONG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to send otp to email
	 */
	@Override
	public ResponseModel sendMailOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ApplicationUserEntity updatedUserDetails = null;
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
			ApplicationUserEntity emailPresent = repository.findByEmailId(userEntity.getEmailId());
			if (isUserPresent.isPresent() && isUserPresent.get().getSmsOtp() > 0 && (emailPresent == null
					|| emailPresent != null && emailPresent.getMobileNo() == isUserPresent.get().getMobileNo())) {
				ApplicationUserEntity oldUserEntity = isUserPresent.get();
				String mapKey = String.valueOf(oldUserEntity.getMobileNo()) + EkycConstants.EMAIL_KEY;
				int otp = commonMethods.generateOTP(oldUserEntity.getMobileNo());
				oldUserEntity.setEmailId(userEntity.getEmailId());
				oldUserEntity.setEmailOtp(otp);
				oldUserEntity.setEmailVerified(0);
				updatedUserDetails = repository.save(oldUserEntity);
				commonMethods.sendMailOtp(otp, userEntity.getEmailId());
				HazleCacheController.getInstance().getVerifyOtp().put(mapKey, otp, 300000, TimeUnit.MILLISECONDS);
				if (updatedUserDetails != null) {
					responseModel = new ResponseModel();
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setResult(updatedUserDetails);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_GENERATE_OTP);
				}
			} else {
				if (emailPresent != null) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_ALREADY_AVAILABLE);
				} else {
					if (isUserPresent.isPresent() && isUserPresent.get().getSmsOtp() == 0) {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.SMS_OTP_NOT_VERIFIED);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.WRONG_USER_ID);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to validate email OTP
	 */
	@Override
	public ResponseModel verifyEmailOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = null;
		try {
			ApplicationUserEntity updatedUserDetails = null;
			ApplicationUserEntity oldUserEntity = repository.findByEmailId(userEntity.getEmailId());
			if (oldUserEntity != null) {
				String mapKey = String.valueOf(oldUserEntity.getMobileNo()) + EkycConstants.EMAIL_KEY;
				if (HazleCacheController.getInstance().getVerifyOtp().containsKey(mapKey)) {
					if (HazleCacheController.getInstance().getVerifyOtp().get(mapKey) == userEntity.getEmailOtp()) {
						oldUserEntity.setEmailVerified(1);
						updatedUserDetails = repository.save(oldUserEntity);
						if (updatedUserDetails != null) {
							ExistingCustReqModel custModel = new ExistingCustReqModel();
							custModel.setInput(oldUserEntity.getEmailId());
							custModel.setInputType(EkycConstants.ERP_EMAIL);
							ErpExistingApiModel existingModel = erpRestService.erpCheckExisting(custModel);
							if (existingModel != null
									&& StringUtil.isEqual(existingModel.getExisting(), EkycConstants.EXISTING_YES)
									&& StringUtil.isNotNullOrEmpty(existingModel.getStatus()) && StringUtil
											.isNotEqual(existingModel.getExisting(), EkycConstants.STATUS_INACTIVE)) {
								if (StringUtil.isNotNullOrEmpty(existingModel.getStatus()) && StringUtil
										.isEqual(existingModel.getExisting(), EkycConstants.STATUS_ACTIVE)) {
									responseModel = commonMethods
											.constructFailedMsg(MessageConstants.EKYC_EMAIL_ACTIVE_CUSTOMER);
								} else if (StringUtil.isNotNullOrEmpty(existingModel.getStatus()) && StringUtil
										.isEqual(existingModel.getExisting(), EkycConstants.STATUS_DORMANT)) {
									responseModel = commonMethods
											.constructFailedMsg(MessageConstants.EKYC_DORMANT_CUSTOMER);
									responseModel.setPage(EkycConstants.PAGE_PDFDOWNLOAD);
								}
							} else {
								responseModel = new ResponseModel();
								responseModel.setMessage(EkycConstants.SUCCESS_MSG);
								responseModel.setStat(EkycConstants.SUCCESS_STATUS);
								responseModel.setResult(updatedUserDetails);
								responseModel.setPage(EkycConstants.PAGE_PAN);
							}
						} else {
							responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_VERIFY_OTP);
						}
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.OTP_TIME_EXPIRED);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_WRONG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

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
		Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
		if (isUserPresent.isPresent()) {
			ApplicationUserEntity oldUserEntity = isUserPresent.get();
			oldUserEntity.setDob(userEntity.getDob());
			oldUserEntity.setStage(2);
			ApplicationUserEntity savingEntity = repository.save(oldUserEntity);
			if (savingEntity != null) {
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

	/**
	 * Method to intialize digi locker
	 */
	@Override
	public ResponseModel iniDigilocker(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
		if (isUserPresent.isPresent()) {
			String redirectUrl = props.getDigiBaseUrl() + EkycConstants.DIGI_CONST_AUTH_CLIENT_ID
					+ props.getDigiClientId() + EkycConstants.DIGI_CONST_RES_TYPE + props.getDigiResponseCode()
					+ EkycConstants.DIGI_CONST_STATE + applicationId;
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(redirectUrl);
			responseModel.setPage(EkycConstants.PAGE_AADHAR);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}

}

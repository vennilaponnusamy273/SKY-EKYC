package in.codifi.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.controller.spec.IPennyController;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApiStatusEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.KraKeyValueEntity;
import in.codifi.api.entity.PennyDropEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.entity.ReferralEntity;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.helper.DeleteHelper;
import in.codifi.api.helper.UserHelper;
import in.codifi.api.model.DocReqModel;
import in.codifi.api.model.RejectionModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApiStatusRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.PennyDropRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.repository.ReferralRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.restservice.keycloak.KeyCloakAdminRestService;
import in.codifi.api.service.spec.IUserService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class UserService implements IUserService {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	UserHelper userHelper;
	@Inject
	AddressRepository repos;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	PennyDropRepository PennyRepository;
	@Inject
	KeyCloakAdminRestService keyCloakAdminRestService;
	@Inject
	DeleteHelper deleteHelper;
	@Inject
	KraKeyValueRepository keyValueRepository;
	@Inject
	IPennyController iPennyController;
	@Inject
	ApiStatusRepository apiStatusRepository;
	@Inject 
	ReferralRepository referralRepository;

	private static final Logger logger = LogManager.getLogger(UserService.class);

	/**
	 * Method to send otp to mobile number
	 */
	public ResponseModel sendSmsOtp(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ApplicationUserEntity updatedUserDetails = null;
			ApplicationUserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			String mapKey = String.valueOf(userEntity.getMobileNo()) + EkycConstants.SMS_KEY;
			if (oldUserEntity == null) {
			/**	String uccCode = commonMethods.generateUccCode();
				if (StringUtil.isNotNullOrEmpty(uccCode)) {
					if (uccCode.length() > 2) {
						userEntity.setUccCodePrefix(uccCode.substring(0, 2));
					}
					if (uccCode.length() > 5) {
						userEntity.setUccCodeSuffix(uccCode.substring(2, 6));
					}
				}
				System.out.println(uccCode);**/
				// send OTP
				updatedUserDetails = userHelper.saveOrUpdateSmsTrigger(userEntity);
			} else {
				// resend OTP
				ResponseModel timeValidation = userHelper.checkOtpTimeValidation(mapKey);
				if (timeValidation == null) {
					updatedUserDetails = userHelper.saveOrUpdateSmsTrigger(oldUserEntity);
				} else {
					return timeValidation;
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
			// e.printStackTrace();
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "UserService", "sendSmsOtp", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In sendSmsOtp for the Error: " + e.getMessage(),
					"ERR-001");
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
						if (oldUserEntity.getEmailVerified() == 0) {
							oldUserEntity.setStage(EkycConstants.PAGE_SMS);
						}
						if (userEntity.getReferralBy() != null&&!userEntity.getReferralBy().isEmpty()&&!userEntity.getReferralBy().isBlank()) {
						    ReferralEntity referralEntity = referralRepository.findByMobileNo(userEntity.getMobileNo());
						    if (referralEntity == null) {
						        referralEntity = new ReferralEntity();
						        referralEntity.setMobileNo(userEntity.getMobileNo());
						    }
						    referralEntity.setReferralBy(userEntity.getReferralBy());
						    referralEntity.setRefByDesignation(userEntity.getReferralType());
						    referralEntity.setRefByName(userEntity.getReferralBy());
						    referralRepository.save(referralEntity);
						}
						updatedUserDetails = repository.save(oldUserEntity);
						HazleCacheController.getInstance().getVerifyOtp().remove(mapKey);
						HazleCacheController.getInstance().getRetryOtp().remove(mapKey);
						HazleCacheController.getInstance().getResendOtp().remove(mapKey);
						if (updatedUserDetails != null && StringUtil.isNotNullOrEmpty(updatedUserDetails.getStatus())
								&& (StringUtil.isEqual(updatedUserDetails.getStatus(),
										EkycConstants.EKYC_STATUS_INPROGRESS)
										|| StringUtil.isEqual(updatedUserDetails.getStatus(),
												EkycConstants.EKYC_STATUS_PDF_GENERATED)
										|| StringUtil.isEqual(updatedUserDetails.getStatus(),
												EkycConstants.EKYC_STATUS_ESIGN_COMPLETED))) {
							responseModel = new ResponseModel();
							responseModel.setMessage(EkycConstants.SUCCESS_MSG);
							responseModel.setStat(EkycConstants.SUCCESS_STATUS);
							responseModel.setPage(getPageNumber(updatedUserDetails));
							responseModel.setResult(updatedUserDetails);
						} else {
							if (updatedUserDetails == null) {
								responseModel = commonMethods
										.constructFailedMsg(MessageConstants.ERROR_WHILE_VERIFY_OTP);
							} else {
								responseModel = new ResponseModel();
								responseModel.setMessage(EkycConstants.SUCCESS_MSG);
								responseModel.setStat(EkycConstants.SUCCESS_STATUS);
								responseModel.setResult(updatedUserDetails);
								responseModel.setPage(EkycConstants.PAGE_EMAIL);
							}
						}
						commonMethods.generateAuthToken(updatedUserDetails);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.OTP_TIME_EXPIRED);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_WRONG);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "UserService", "verifySmsOtp", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In verifySmsOtp for the Error: " + e.getMessage(),
					"ERR-001");
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
			/**List<GetUserInfoResp> emailExist = keyCloakAdminRestService.getUserInfoByAttribute("email",
					userEntity.getEmailId());
			if (emailExist != null && emailExist.size() > 0)
				return commonMethods.constructFailedMsg(MessageConstants.KEYCLOAK_EMAIL_EXIST);**/
			ApplicationUserEntity emailPresent = repository.findByEmailId(userEntity.getEmailId());
			if (isUserPresent.isPresent() && isUserPresent.get().getSmsVerified() > 0 && (emailPresent == null
					|| emailPresent != null && emailPresent.getMobileNo() == isUserPresent.get().getMobileNo())) {
				ApplicationUserEntity oldUserEntity = isUserPresent.get();
				String mapKey = String.valueOf(oldUserEntity.getMobileNo()) + EkycConstants.EMAIL_KEY;
				ResponseModel timeValidation = userHelper.checkOtpTimeValidation(mapKey);
				if (timeValidation == null) {
					int otp = commonMethods.generateOTP(oldUserEntity.getMobileNo());
					oldUserEntity.setEmailId(userEntity.getEmailId());
					oldUserEntity.setEmailOtp(otp);
					oldUserEntity.setEmailVerified(0);
					updatedUserDetails = repository.save(oldUserEntity);
					commonMethods.sendMailOtp(otp, userEntity.getEmailId());
					HazleCacheController.getInstance().getVerifyOtp().put(mapKey, otp, 3600, TimeUnit.SECONDS);
					if (updatedUserDetails != null) {
						responseModel = new ResponseModel();
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
						responseModel.setPage(EkycConstants.PAGE_EMAIL);
						responseModel.setResult(updatedUserDetails);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_GENERATE_OTP);
					}
				} else {
					return timeValidation;
				}
			} else {
				if (emailPresent != null) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.EMAIL_ID_ALREADY_AVAILABLE);
				} else {
					if (isUserPresent.isPresent() && isUserPresent.get().getSmsVerified() == 0) {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.SMS_OTP_NOT_VERIFIED);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.WRONG_USER_ID);
					}
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "UserService", "sendMailOtp", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In sendMailOtp for the Error: " + e.getMessage(),
					"ERR-001");
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
						oldUserEntity.setStage(EkycConstants.PAGE_EMAIL);
						oldUserEntity.setStatus(EkycConstants.EKYC_STATUS_INPROGRESS);
						updatedUserDetails = repository.save(oldUserEntity);
						if (updatedUserDetails != null) {
							responseModel = new ResponseModel();
							responseModel.setMessage(EkycConstants.SUCCESS_MSG);
							responseModel.setStat(EkycConstants.SUCCESS_STATUS);
							responseModel.setResult(updatedUserDetails);
							responseModel.setPage(EkycConstants.PAGE_PASSWORD);
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
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "UserService", "verifyEmailOtp", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request, In verifyEmailOtp for the Error: "
							+ e.getMessage(), "ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to get User details
	 */

	@Override
	public ResponseModel getUserDetailsById(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
			AddressEntity isAddressisPresent = repos.findByapplicationId(applicationId);
			if (isAddressisPresent != null) {
				responseModel.setAddress_response(isAddressisPresent);
			} else {
				responseModel.setAddress_response(MessageConstants.ADDRESS_NOT_YET);
			}
			List<ApiStatusEntity> apiStatusEntity = apiStatusRepository.findByApplicationIdAndStatus(applicationId, 0);
			if (StringUtil.isListNotNullOrEmpty(apiStatusEntity)) {
				List<RejectionModel> rejectionModels = constructRejectionModel(apiStatusEntity, applicationId);
				responseModel.setRejectionUser(rejectionModels);
			}
			ProfileEntity profileEntity = profileRepository.findByapplicationId(applicationId);
			if (isUserPresent.isPresent()) {
				if (profileEntity != null && StringUtil.isNotNullOrEmpty(profileEntity.getGender())) {
					isUserPresent.get().setGender(profileEntity.getGender());
				}
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(isUserPresent);
				responseModel.setPage(getPageNumber(isUserPresent.get()));
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "UserService", "getUserDetailsById", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In getUserDetailsById for the Error: "
							+ e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to get Documents that need to upload
	 */
	@Override
	public ResponseModel docStatus(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			iPennyController.ValidateDetails(applicationId);
			Optional<ApplicationUserEntity> user = repository.findById(applicationId);
			PennyDropEntity PennyUser = PennyRepository.findByapplicationId(applicationId);
			SegmentEntity savedSegmentEntity = segmentRepository.findByapplicationId(applicationId);
			List<KraKeyValueEntity> list = keyValueRepository.findByMasterIdAndMasterName("6", "DOCUMENT_UPLOAD");
			List<String> proofType = new ArrayList<>();
			if (StringUtil.isListNotNullOrEmpty(list)) {
				list.forEach(a -> {
					proofType.add(a.getKraValue());
				});
			}
			DocReqModel docReqModel = new DocReqModel();
			docReqModel.setProofTypes(proofType);
			if (user != null) {
				if (savedSegmentEntity != null) {
					if (savedSegmentEntity.getEd() == 0 && savedSegmentEntity.getCd() == 0
							&& savedSegmentEntity.getComm() == 0) {
						docReqModel.setIncomeProofRequired(false);
					}
				}
				if (PennyUser != null && StringUtil.isNotNullOrEmpty(PennyUser.getAccountHolderName())) {
					String accountHolderName = PennyUser.getAccountHolderName();
					String firstname = user.get().getFirstName();
					String lastname = user.get().getLastName();
					String middleName = user.get().getMiddleName();
					String fullName = user.get().getUserName();
					if ((StringUtil.isNotNullOrEmpty(firstname)
							&& accountHolderName.toLowerCase().contains(firstname.toLowerCase()))
							|| (StringUtil.isNotNullOrEmpty(lastname)
									&& accountHolderName.toLowerCase().contains(lastname.toLowerCase()))
							|| (StringUtil.isNotNullOrEmpty(middleName)
									&& accountHolderName.toLowerCase().contains(middleName.toLowerCase()))
							|| (StringUtil.isNotNullOrEmpty(fullName)
									&& accountHolderName.toLowerCase().contains(fullName.toLowerCase()))) {
						docReqModel.setChequeRequired(false);
						docReqModel.setNameMismatch(false);
					}
				}
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(docReqModel);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "UserService", "docStatus", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In docStatus for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to create new user in keycloak
	 */
	@Override
	public ResponseModel userCreation(ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		/**try {
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
			if (isUserPresent.isPresent()) {
				List<GetUserInfoResp> emailExist = keyCloakAdminRestService.getUserInfoByAttribute("mobile",
						isUserPresent.get().getMobileNo().toString());
				if (emailExist != null && emailExist.size() > 0)
					return commonMethods.constructFailedMsg(MessageConstants.KEYCLOAK_MOBILE_EXIST);
				ApplicationUserEntity savingEntity = isUserPresent.get();
				savingEntity.setPassword(userEntity.getPassword());
				ApplicationUserEntity savedEntity = repository.save(savingEntity);
				if (savedEntity != null) {
					CreateUserRequestModel requestModel = new CreateUserRequestModel();
					List<CreateUserCredentialsModel> userCredentilList = new ArrayList<>();
					requestModel.setEmail(savedEntity.getEmailId());
					requestModel.setUsername(savedEntity.getMobileNo().toString());
					requestModel.setFirstName("Guest");
					requestModel.setLastName("User");
					requestModel.setEnabled(true);
					requestModel.setEmailVerified(true);
					CreateUserCredentialsModel credentialsModel = new CreateUserCredentialsModel();
					credentialsModel.setType("password");
					credentialsModel.setValue(savedEntity.getPassword());
					userCredentilList.add(credentialsModel);
					requestModel.setCredentials(userCredentilList);
					String message = keyCloakAdminRestService.addNewUser(requestModel);
					if (StringUtil.isNotNullOrEmpty(message)) {
						responseModel.setReason(message);**/
						commonMethods.UpdateStep(EkycConstants.PAGE_PASSWORD, userEntity.getId());
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
						responseModel.setPage(EkycConstants.PAGE_PAN);
					/**} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.INTERNAL_SERVER_ERROR);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(userEntity.getId(), "UserService", "userCreation", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In userCreation for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}**/
		return responseModel;
	}

	/**
	 * Method to star over the application
	 */
	@Override
	public ResponseModel startOver(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = deleteHelper.DeleteAll(applicationUserEntity);
		return responseModel;
	}

	public String getPageNumber(ApplicationUserEntity applicationUserEntity) {
		int key = 0;
		try {
			for (Entry<Integer, String> entry : HazleCacheController.getInstance().getPageDetail().entrySet()) {
				if (applicationUserEntity.getStage().equals(entry.getValue())) {
					key = entry.getKey();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationUserEntity.getId(), "UserService", "getPageNumber", e.getMessage());
			commonMethods
					.sendErrorMail("An error occurred while processing your request, In getPageNumber for the Error: "
							+ e.getMessage(), "ERR-001");
		}
		return HazleCacheController.getInstance().getPageDetail().get(key + 1);
	}

	private List<RejectionModel> constructRejectionModel(List<ApiStatusEntity> apiStatusEntities, long applicationId) {
		List<RejectionModel> rejectionModels = new ArrayList<>();
		repository.updateRejectionStage(applicationId, 0, 0, EkycConstants.PAGE_ESIGN);
		for (ApiStatusEntity entity : apiStatusEntities) {
			if (entity != null) {
				RejectionModel model = new RejectionModel();
				model.setRejectedPage(entity.getStage());
				model.setRejectedReason(entity.getReason());
				if (StringUtil.isNotNullOrEmpty(entity.getDocType())) {
					model.setDocType(entity.getDocType());
				}
				rejectionModels.add(model);
			}
		}
		if (StringUtil.isListNotNullOrEmpty(apiStatusEntities)) {
			RejectionModel model = new RejectionModel();
			model.setRejectedPage("12");
			model.setRejectedReason("Esign Needed");
			rejectionModels.add(model);
		}
		return rejectionModels;
	}

}

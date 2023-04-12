package in.codifi.api.service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.model.CkycRequestApiModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.ckyc.CkycResponse;
import in.codifi.api.model.ckyc.PersonalDetails;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.CkycResponseRepos;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.restservice.AryaLivenessCheck;
import in.codifi.api.service.spec.ICkycService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;

@Service
public class CkycService implements ICkycService {

	@Inject
	CommonMethods commonMethods;

	@Inject
	CkycResponseRepos repos;

	@Inject
	ApplicationProperties props;

	@Inject
	ApplicationUserRepository repository;

	@Inject
	ProfileRepository profileRepository;

	@Inject
	AryaLivenessCheck aryaLivenessCheck;

	private static final Logger logger = LogManager.getLogger(CkycService.class);
	
	public ResponseModel saveCkycResponse(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				ResponseCkyc checkExit = repos.findByApplicationId(applicationId);
				ProfileEntity profileDetails = profileRepository.findByapplicationId(applicationId);
				CkycRequestApiModel ckycRequest = buildCkycRequest(isUserPresent.get());
				CkycResponse ckycResponse = aryaLivenessCheck.getCKycData(ckycRequest);
				commonMethods.reqResSaveObject(ckycRequest, ckycResponse, EkycConstants.CKYC, applicationId);
				if (ckycResponse.getSuccess()) {
					ExecutorService pool = Executors.newSingleThreadExecutor();
					pool.execute(new Runnable() {
						@Override
						public void run() {
							if (ckycResponse != null && ckycResponse.getResult().getPersonalDetails() != null) {
								PersonalDetails personalDetails = ckycResponse.getResult().getPersonalDetails();
								ResponseCkyc response = buildCkycResponse(personalDetails, applicationId, checkExit);
								repos.save(response);
								String motherName = "";
								if (StringUtil.isNotNullOrEmpty(personalDetails.getMotherFname())
										|| StringUtil.isNotNullOrEmpty(personalDetails.getMotherLname())) {
									motherName = personalDetails.getMotherFname()
											+ (StringUtil.isNotNullOrEmpty(personalDetails.getMotherLname())
													? " " + personalDetails.getMotherLname()
													: "");
								} else if (StringUtil.isNotNullOrEmpty(personalDetails.getMotherFullname())) {
									motherName = personalDetails.getMotherFullname();
								}
								if (StringUtil.isNotNullOrEmpty(motherName)) {
									if (profileDetails != null
											&& StringUtil.isNullOrEmpty(profileDetails.getMotherName())) {
										profileDetails.setMotherName(motherName);
										profileRepository.save(profileDetails);
									}
								}
							}
						}
					});
					pool.shutdown();
				} else {
					responseModel = commonMethods
							.constructFailedMsg(ckycResponse.getAdditionalProperties().get("message").toString());
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to build CKYC Request
	 * 
	 * @param userEntity
	 * @return
	 */
	public CkycRequestApiModel buildCkycRequest(ApplicationUserEntity userEntity) {
		CkycRequestApiModel ckycRequestApiModel = new CkycRequestApiModel();
		try {
		ckycRequestApiModel.setId_type(EkycConstants.PAN_TYPE);
		ckycRequestApiModel.setId_num(userEntity.getPanNumber());
		ckycRequestApiModel.setFull_name(userEntity.getUserName());
		ckycRequestApiModel.setGender(userEntity.getGender());
		ckycRequestApiModel.setDob(userEntity.getDob());
		ckycRequestApiModel.setReq_id(Long.toString(userEntity.getId()));
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return ckycRequestApiModel;
	}

	/**
	 * Method to build CKYC Response
	 * 
	 * @param personalDetails
	 * @param applicationId
	 * @param checkExit
	 * @return
	 */
	public ResponseCkyc buildCkycResponse(PersonalDetails personalDetails, long applicationId, ResponseCkyc checkExit) {
		ResponseCkyc response = new ResponseCkyc();
		try
		{
		response.setApplicationId(applicationId);
		response.setAccType(personalDetails.getAccType());
		response.setCkycNo(personalDetails.getCkycNo());
		response.setConstiType(personalDetails.getConstiType());
		response.setCorresCity(personalDetails.getCorresCity());
		response.setCorresCountry(personalDetails.getCorresCountry());
		response.setCorresDist(personalDetails.getCorresDist());
		response.setCorresLine1(personalDetails.getCorresLine1());
		response.setCorresLine2(personalDetails.getCorresLine2());
		response.setCorresLine3(personalDetails.getCorresLine3());
		response.setCorresPin(personalDetails.getCorresPin());
		response.setCorresPoa(personalDetails.getCorresPoa());
		response.setCorresState(personalDetails.getCorresState());
		response.setDecDate(personalDetails.getDecDate());
		response.setDecPlace(personalDetails.getDecPlace());
		response.setDob(personalDetails.getDob());
		response.setDocSub(personalDetails.getDocSub());
		response.setEmail(personalDetails.getEmail());
		response.setFatherFname(personalDetails.getFatherFname());
		response.setFatherFullname(personalDetails.getFatherFullname());
		response.setFatherLname((String) personalDetails.getFatherLname());
		response.setFatherMname((String) personalDetails.getFatherMname());
		response.setFatherPrefix(personalDetails.getFatherPrefix());
		response.setFname(personalDetails.getFname());
		response.setFatherFullname(personalDetails.getFatherFullname());
		response.setGender(personalDetails.getGender());
		response.setKycDate(personalDetails.getKycDate());
		response.setLname(personalDetails.getLname());
		response.setMobCode(personalDetails.getMobCode());
		response.setMobNum(personalDetails.getMobNum());
		response.setMotherFname(personalDetails.getMotherFname());
		response.setMotherFullname(personalDetails.getMotherFullname());
		response.setMotherLname((String) personalDetails.getMotherLname());
		response.setMotherMname((String) personalDetails.getMotherMname());
		response.setMotherPrefix(personalDetails.getMotherPrefix());
		response.setNumIdentity(personalDetails.getNumIdentity());
		response.setNumImages(personalDetails.getNumImages());
		response.setNumRelated(personalDetails.getNumRelated());
		response.setOrgCode(personalDetails.getOrgCode());
		response.setOrgName(personalDetails.getOrgName());
		response.setPan(personalDetails.getPan());
		response.setPermCity(personalDetails.getPermCity());
		response.setPermCorresSameflag(personalDetails.getPermCorresSameflag());
		response.setPermCountry(personalDetails.getPermCountry());
		response.setPermDist(personalDetails.getPermDist());
		response.setPermLine1(personalDetails.getPermLine1());
		response.setPermLine2(personalDetails.getPermLine2());
		response.setPermPin(personalDetails.getPermPin());
		response.setPermPoa(personalDetails.getPermPoa());
		response.setPermState(personalDetails.getPermState());
		response.setPrefix(personalDetails.getPrefix());
		response.setRemarks((String) personalDetails.getRemarks());
		response.setUpdatedDate(personalDetails.getUpdatedDate());
		if (checkExit != null && checkExit.getId() != null && checkExit.getId() > 0) {
			response.setId(checkExit.getId());
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return response;
	}
}

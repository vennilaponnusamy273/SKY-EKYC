package in.codifi.api.service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

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

	public ResponseModel saveCkycResponse(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				ExecutorService pool = Executors.newSingleThreadExecutor();
				pool.execute(new Runnable() {
					@Override
					public void run() {
						String motherName = "";
						CkycRequestApiModel ckycRequestApiModel = new CkycRequestApiModel();
						ckycRequestApiModel.setId_type(EkycConstants.PAN_TYPE);
						ckycRequestApiModel.setId_num(isUserPresent.get().getPanNumber());
						ckycRequestApiModel.setFull_name(isUserPresent.get().getUserName());
						ckycRequestApiModel.setGender(isUserPresent.get().getGender());
						ckycRequestApiModel.setDob(isUserPresent.get().getDob());
						ckycRequestApiModel.setReq_id(Long.toString(applicationId));
						CkycResponse dto = aryaLivenessCheck.getCKycData(ckycRequestApiModel);
						if (dto != null && dto.getResult().getPersonalDetails() != null) {
							ResponseCkyc checkExit = repos.findByapplicationId(applicationId);
							if (checkExit == null) {
								PersonalDetails personalDetails = dto.getResult().getPersonalDetails();
								ResponseCkyc response = new ResponseCkyc();
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
								if (StringUtil.isNotNullOrEmpty(personalDetails.getMotherFullname())) {
									motherName = personalDetails.getMotherFullname();
								}
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
								repos.save(response);
								responseModel.setResult(response);
								// commonMethods.UpdateStep(1,ckyc.getApplicationid());

							} else {
								PersonalDetails personalDetails = dto.getResult().getPersonalDetails();
								checkExit.setAccType(personalDetails.getAccType());
								checkExit.setCkycNo(personalDetails.getCkycNo());
								checkExit.setConstiType(personalDetails.getConstiType());
								checkExit.setCorresCity(personalDetails.getCorresCity());
								checkExit.setCorresCountry(personalDetails.getCorresCountry());
								checkExit.setCorresDist(personalDetails.getCorresDist());
								checkExit.setCorresLine1(personalDetails.getCorresLine1());
								checkExit.setCorresLine2(personalDetails.getCorresLine2());
								checkExit.setCorresLine3(personalDetails.getCorresLine3());
								checkExit.setCorresPin(personalDetails.getCorresPin());
								checkExit.setCorresPoa(personalDetails.getCorresPoa());
								checkExit.setCorresState(personalDetails.getCorresState());
								checkExit.setDecDate(personalDetails.getDecDate());
								checkExit.setDecPlace(personalDetails.getDecPlace());
								checkExit.setDob(personalDetails.getDob());
								checkExit.setDocSub(personalDetails.getDocSub());
								checkExit.setEmail(personalDetails.getEmail());
								checkExit.setFatherFname(personalDetails.getFatherFname());
								checkExit.setFatherFullname(personalDetails.getFatherFullname());
								checkExit.setFatherLname((String) personalDetails.getFatherLname());
								checkExit.setFatherMname((String) personalDetails.getFatherMname());
								checkExit.setFatherPrefix(personalDetails.getFatherPrefix());
								checkExit.setFname(personalDetails.getFname());
								checkExit.setFatherFullname(personalDetails.getFatherFullname());
								checkExit.setGender(personalDetails.getGender());
								checkExit.setKycDate(personalDetails.getKycDate());
								checkExit.setLname(personalDetails.getLname());
								checkExit.setMobCode(personalDetails.getMobCode());
								checkExit.setMobNum(personalDetails.getMobNum());
								checkExit.setMotherFname(personalDetails.getMotherFname());
								checkExit.setMotherFullname(personalDetails.getMotherFullname());
								if (StringUtil.isNotNullOrEmpty(personalDetails.getMotherFullname())) {
									motherName = personalDetails.getMotherFullname();
								}
								checkExit.setMotherLname((String) personalDetails.getMotherLname());
								checkExit.setMotherMname((String) personalDetails.getMotherMname());
								checkExit.setMotherPrefix(personalDetails.getMotherPrefix());
								checkExit.setNumIdentity(personalDetails.getNumIdentity());
								checkExit.setNumImages(personalDetails.getNumImages());
								checkExit.setNumRelated(personalDetails.getNumRelated());
								checkExit.setOrgCode(personalDetails.getOrgCode());
								checkExit.setOrgName(personalDetails.getOrgName());
								checkExit.setPan(personalDetails.getPan());
								checkExit.setPermCity(personalDetails.getPermCity());
								checkExit.setPermCorresSameflag(personalDetails.getPermCorresSameflag());
								checkExit.setPermCountry(personalDetails.getPermCountry());
								checkExit.setPermDist(personalDetails.getPermDist());
								checkExit.setPermLine1(personalDetails.getPermLine1());
								checkExit.setPermLine2(personalDetails.getPermLine2());
								checkExit.setPermPin(personalDetails.getPermPin());
								checkExit.setPermPoa(personalDetails.getPermPoa());
								checkExit.setPermState(personalDetails.getPermState());
								checkExit.setPrefix(personalDetails.getPrefix());
								checkExit.setRemarks((String) personalDetails.getRemarks());
								checkExit.setUpdatedDate(personalDetails.getUpdatedDate());
								repos.save(checkExit);
								responseModel.setResult(checkExit);
							}
							if (StringUtil.isNotNullOrEmpty(motherName)) {
								ProfileEntity profileDetails = profileRepository.findByapplicationId(applicationId);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;

	}
}

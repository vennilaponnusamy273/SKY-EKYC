package in.codifi.api.service;

import java.util.Optional;

import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.ckyc.CkycResponse;
import in.codifi.api.model.ckyc.PersonalDetails;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.CkycResponseRepos;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.service.spec.ICkycService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

	@Override
	public ResponseModel getckyc(long applicationId) {

		ResponseModel responseModel = new ResponseModel();
		try {
			if (applicationId > 0) {
				Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
				if (isUserPresent.isPresent()) {
					OkHttpClient client = new OkHttpClient().newBuilder().build();
					MediaType mediaType = MediaType.parse(EkycConstants.CONST_APPLICATION_JSON);
					// RequestBody body = RequestBody.create(mediaType, "{\r\n \"id_type\":
					// \"PAN\",\r\n \"id_num\": \"CKAPD5698F\",\r\n \"full_name\": \"DINESH\",\r\n
					// \"gender\": \"M\",\r\n \"dob\": \"24-01-1995\",\r\n \"req_id\": \"1\"\r\n}");
					RequestBody body = RequestBody.create(mediaType, "{\r\n    \"id_type\": \"" + EkycConstants.PAN_TYPE
							+ "\",\r\n    \"id_num\": \"" + isUserPresent.get().getPanNumber()
							+ "\",\r\n    \"full_name\": \"" + isUserPresent.get().getUserName()
							+ "\",\r\n    \"gender\": \"" + isUserPresent.get().getGender() + "\",\r\n    \"dob\": \""
							+ isUserPresent.get().getDob() + "\",\r\n    \"req_id\": \"" + applicationId + "\"\r\n}");
					Request request = new Request.Builder().url(props.getCkycapi())
							.method(EkycConstants.HTTP_POST, body)
							.addHeader(EkycConstants.DIGI_CONST_TOKEN, EkycConstants.CKYC_TOKEN)
							.addHeader(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONST_APPLICATION_JSON)
							.build();
					Response response = client.newCall(request).execute();
					if (response.code() == 200) {
						ObjectMapper om = new ObjectMapper();
						CkycResponse dto = om.readValue(response.body().string(), CkycResponse.class);
						responseModel.setResult(saveCkycResponse(dto, applicationId));
					} else {
						System.out.println(response.code());

					}
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.INVLAID_PARAMETER);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	public ResponseModel saveCkycResponse(CkycResponse dto, long applicatioId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			String motherName = "";
			ResponseCkyc checkExit = repos.findByapplicationId(applicatioId);
			if (checkExit == null) {
				PersonalDetails personalDetails = dto.getResult().getPersonalDetails();
				ResponseCkyc response = new ResponseCkyc();
				response.setApplicationId(applicatioId);
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
				ProfileEntity profileDetails = profileRepository.findByapplicationId(applicatioId);
				if (profileDetails != null && StringUtil.isNullOrEmpty(profileDetails.getMotherName())) {
					profileDetails.setMotherName(motherName);
					profileRepository.save(profileDetails);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;

	}
}

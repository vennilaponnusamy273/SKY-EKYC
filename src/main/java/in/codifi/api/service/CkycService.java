package in.codifi.api.service;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.restservice.AryaLivenessCheck;
import in.codifi.api.service.spec.ICkycService;
import in.codifi.api.utilities.CommonMethods;

@Service
public class CkycService implements ICkycService {
//	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	CommonMethods commonMethods;

//	@Inject
//	CkycResponseRepos repos;

	@Inject
	ApplicationProperties props;

	@Inject
	ApplicationUserRepository repository;

	@Inject
	ProfileRepository profileRepository;

	@Inject
	AryaLivenessCheck aryaLivenessCheck;
	
	@Inject
	DocumentService documentService;

	@Inject
	DocumentRepository docrepository;
	
	@Inject
	AddressRepository addressRepository;
	
	private static final Logger logger = LogManager.getLogger(CkycService.class);
	
//	public ResponseModel saveCkycResponse(long applicationId) {
//		ResponseModel responseModel = new ResponseModel();
//		try {
//			Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
//			if (isUserPresent.isPresent()) {
//				//ResponseCkyc checkExit = repos.findByApplicationId(applicationId);
//				ProfileEntity profileDetails = profileRepository.findByapplicationId(applicationId);
//				CkycRequestApiModel ckycRequest = buildCkycRequest(isUserPresent.get());
//				CkycResponse ckycResponse = aryaLivenessCheck.getCKycData(ckycRequest);
//				commonMethods.reqResSaveObject(ckycRequest, ckycResponse, EkycConstants.CKYC, applicationId);
//				if (ckycResponse.getSuccess()) {
//					/**ExecutorService pool = Executors.newSingleThreadExecutor();
//					pool.execute(new Runnable() {
//						@Override
//						public void run() {**/
//							if (ckycResponse != null && ckycResponse.getResult().getPersonalDetails() != null) {
//								PersonalDetails personalDetails = ckycResponse.getResult().getPersonalDetails();							
//								 buildCkycResponse(personalDetails, applicationId);
//								ImageDetails imagedetails=ckycResponse.getResult().getImageDetails();
//								 storeCkycImage(imagedetails, applicationId);
//								String motherName = "";
//								if (StringUtil.isNotNullOrEmpty(personalDetails.getMotherFname())
//										|| StringUtil.isNotNullOrEmpty(personalDetails.getMotherLname())) {
//									motherName = personalDetails.getMotherFname()
//											+ (StringUtil.isNotNullOrEmpty(personalDetails.getMotherLname())
//													? " " + personalDetails.getMotherLname()
//													: "");
//								} else if (StringUtil.isNotNullOrEmpty(personalDetails.getMotherFullname())) {
//									motherName = personalDetails.getMotherFullname();
//								}
//								if (StringUtil.isNotNullOrEmpty(motherName)) {
//									if (profileDetails != null
//											&& StringUtil.isNullOrEmpty(profileDetails.getMotherName())) {
//										profileDetails.setMotherName(motherName);
//										profileRepository.save(profileDetails);
//									}
//								}
//							}else {
//								AddressEntity addressEntity = addressRepository.findByapplicationId(applicationId);
//								addressEntity.setIsKra(0);
//								addressRepository.save(addressEntity);
//							}
//				/**	});
//					pool.shutdown();**/
//				} else {
//					responseModel = commonMethods
//							.constructFailedMsg(ckycResponse.getAdditionalProperties().get("message").toString());
//				}
//			}
//		} catch (Exception e) {
//			logger.error("An error occurred: " + e.getMessage());
//			commonMethods.SaveLog(applicationId,"CkycService","saveCkycResponse",e.getMessage());
//			commonMethods.sendErrorMail("An error occurred while processing your request, In saveCkycResponse for the Error: " + e.getMessage(),"ERR-001");
//			responseModel = commonMethods.constructFailedMsg(e.getMessage());
//		}
//		return responseModel;
//	}

	/**
	 * Method to build CKYC Request
	 * 
	 * @param userEntity
	 * @return
	 */
//	public ResponseCkyc storeCkycImage(ImageDetails imageDetails, long applicationId)
//			throws FileNotFoundException, IOException {
//		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
//		if (OS.contains(EkycConstants.OS_WINDOWS)) {
//			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
//		}
//		List<Image> images = imageDetails.getImage();
//		if (images != null) {
//			for (Image image : images) {
//				String ImageName=null;
//				if (image.getImageName().equalsIgnoreCase("Signature")){
//					ImageName="ckycSignature";
//				}else if (image.getImageName().equalsIgnoreCase("pan")){
//					ImageName="ckycPan";
//				}
//				else {
//					ImageName=image.getImageName();
//				}
//				String fileName = applicationId + EkycConstants.UNDERSCORE + ImageName+"."
//						+ image.getImageType();
//				byte[] decodedBytes = Base64.getDecoder().decode(image.getImageData());
//				String outputPath = props.getFileBasePath() + applicationId + slash + fileName;
//				if (image.getImageType().equalsIgnoreCase("pdf")) {
//					File file = new File(outputPath);;
//					FileOutputStream fop = new FileOutputStream(file);
//					fop.write(decodedBytes);
//					fop.flush();
//					fop.close();
//					saveDoc(applicationId, fileName, outputPath, ImageName);
//				}
//				else {
//					try (FileOutputStream fos = new FileOutputStream(outputPath)) {
//						fos.write(decodedBytes);
//						saveDoc(applicationId, fileName, outputPath, ImageName);
//					}
//				}
//			}
//		}
//		return null;
//	}
	
	
	
//     public void saveDoc(long applicationId,String fileName, String outputPath, String imageName) {
//    	 DocumentEntity documentEntity=new DocumentEntity();
//    	 	DocumentEntity oldRecord = docrepository.findByApplicationIdAndDocumentType(applicationId,
//    	 			imageName);
//    	 	if (oldRecord!=null) {
//    	 		documentEntity.setId(oldRecord.getId());
//    	 		documentEntity=oldRecord;
//    	 	}
//    	 	documentEntity.setAttachementUrl(outputPath);
//    	 	documentEntity.setDocumentType(imageName);
//    	 	documentEntity.setAttachement(fileName);
//    	 	documentEntity.setApplicationId(applicationId);
//    	 	documentEntity.setTypeOfProof(imageName);
//    	 	docrepository.save(documentEntity);
//	}
	/**public CkycRequestApiModel buildCkycRequest(ApplicationUserEntity userEntity) {
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
			commonMethods.SaveLog(userEntity.getId(),"CkycService","buildCkycRequest",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In buildCkycRequest for the Error: " + e.getMessage(),"ERR-001");
		}
		return ckycRequestApiModel;
	}**/

	/**
	 * Method to build CKYC Response
	 * 
	 * @param personalDetails
	 * @param applicationId
	 * @param checkExit
	 * @return
	 */
//	public ResponseCkyc buildCkycResponse(PersonalDetails personalDetails, long applicationId) {
//		ResponseCkyc response = new ResponseCkyc();
//		try
//		{
//		ResponseCkyc checkExit = repos.findByApplicationId(applicationId);
//		if (checkExit != null) {
//			response.setId(checkExit.getId());
//			response=checkExit;
//		}
//		response.setApplicationId(applicationId);
//		response.setAccType(personalDetails.getAccType());
//		response.setCkycNo(personalDetails.getCkycNo());
//		response.setConstiType(personalDetails.getConstiType());
//		response.setCorresCity(personalDetails.getCorresCity());
//		response.setCorresCountry(personalDetails.getCorresCountry());
//		response.setCorresDist(personalDetails.getCorresDist());
//		response.setCorresLine1(personalDetails.getCorresLine1());
//		response.setCorresLine2(personalDetails.getCorresLine2());
//		response.setCorresLine3(personalDetails.getCorresLine3());
//		response.setCorresPin(personalDetails.getCorresPin());
//		response.setCorresPoa(personalDetails.getCorresPoa());
//		response.setCorresState(personalDetails.getCorresState());
//		response.setDecDate(personalDetails.getDecDate());
//		response.setDecPlace(personalDetails.getDecPlace());
//		response.setDob(personalDetails.getDob());
//		response.setDocSub(personalDetails.getDocSub());
//		response.setEmail(personalDetails.getEmail());
//		response.setFatherFname(personalDetails.getFatherFname());
//		response.setFatherFullname(personalDetails.getFatherFullname());
//		response.setFatherLname((String) personalDetails.getFatherLname());
//		response.setFatherMname((String) personalDetails.getFatherMname());
//		response.setFatherPrefix(personalDetails.getFatherPrefix());
//		response.setFname(personalDetails.getFname());
//		response.setFatherFullname(personalDetails.getFatherFullname());
//		response.setGender(personalDetails.getGender());
//		response.setKycDate(personalDetails.getKycDate());
//		response.setLname(personalDetails.getLname());
//		response.setMobCode(personalDetails.getMobCode());
//		response.setMobNum(personalDetails.getMobNum());
//		response.setMotherFname(personalDetails.getMotherFname());
//		response.setMotherFullname(personalDetails.getMotherFullname());
//		response.setMotherLname((String) personalDetails.getMotherLname());
//		response.setMotherMname((String) personalDetails.getMotherMname());
//		response.setMotherPrefix(personalDetails.getMotherPrefix());
//		response.setNumIdentity(personalDetails.getNumIdentity());
//		response.setNumImages(personalDetails.getNumImages());
//		response.setNumRelated(personalDetails.getNumRelated());
//		response.setOrgCode(personalDetails.getOrgCode());
//		response.setOrgName(personalDetails.getOrgName());
//		response.setPan(personalDetails.getPan());
//		response.setPermCity(personalDetails.getPermCity());
//		response.setPermCorresSameflag(personalDetails.getPermCorresSameflag());
//		response.setPermCountry(personalDetails.getPermCountry());
//		response.setPermDist(personalDetails.getPermDist());
//		response.setPermLine1(personalDetails.getPermLine1());
//		response.setPermLine2(personalDetails.getPermLine2());
//		response.setPermPin(personalDetails.getPermPin());
//		response.setPermPoa(personalDetails.getPermPoa());
//		response.setPermState(personalDetails.getPermState());
//		response.setPrefix(personalDetails.getPrefix());
//		response.setRemarks((String) personalDetails.getRemarks());
//		response.setUpdatedDate(personalDetails.getUpdatedDate());
//		response.setApplicationId(applicationId);
//		
//		repos.save(response);
//		} catch (Exception e) {
//			logger.error("An error occurred: " + e.getMessage());
//			commonMethods.SaveLog(applicationId,"CkycService","buildCkycResponse",e.getMessage());
//			commonMethods.sendErrorMail("An error occurred while processing your request, In buildCkycResponse for the Error: " + e.getMessage(),"ERR-001");
//		}
//		return response;
//	}
}

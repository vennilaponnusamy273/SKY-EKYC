package in.codifi.api.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.GuardianEntity;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.helper.RejectionStatusHelper;
import in.codifi.api.model.NomineeDocModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.GuardianRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.service.spec.INomineeService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class NomineeService implements INomineeService {

	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	NomineeRepository nomineeRepository;
	@Inject
	GuardianRepository guardianRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationProperties props;
	@Inject
	RejectionStatusHelper rejectionStatusHelper;
	@Inject
	DocumentRepository docrepository;
	private static final Logger logger = LogManager.getLogger(NomineeService.class);
	/**
	 * Method to get Nominee Details
	 * 
	 * @author prade
	 **/
	@Override
	public ResponseModel getNominee(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		List<NomineeEntity> savedEntity = populateNomineeAndGuardian(applicationId);
		if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedEntity);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId,"NomineeService","getNominee",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In getNominee for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to populate nominee and Guardian Details
	 * 
	 * @param applicationId
	 * @author prade
	 * @return
	 */
	public List<NomineeEntity> populateNomineeAndGuardian(long applicationId) {
		List<NomineeEntity> savedEntity = nomineeRepository.findByapplicationId(applicationId);
		try {
		if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
			savedEntity.forEach(entity -> {
				entity.setGuardianEntity(guardianRepository.findByNomineeId(entity.getId()));
			});
		}
		} catch (Exception e) {
			commonMethods.sendErrorMail("An error occurred while processing your request, In populateNomineeAndGuardian for the Error: " + e.getMessage(),"ERR-001");
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId,"NomineeService","populateNomineeAndGuardian",e.getMessage());
		}
		return savedEntity;
	}

	/**
	 * Method to upload nominee details
	 * 
	 * @param fileModel
	 * @author prade
	 * @return
	 */
	@Override
	public ResponseModel uploadDocNominee(NomineeDocModel fileModel) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (fileModel.getNomFile() != null && StringUtil.isNotNullOrEmpty(fileModel.getNomFile().contentType())) {
				String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
				if (OS.contains(EkycConstants.OS_WINDOWS)) {
					slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
				}
				File dir = new File(props.getFileBasePath() + fileModel.getApplicationId());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				Long countNominee = nomineeRepository.countByApplicationId(fileModel.getApplicationId());
				String nomineeId="Nominee_" + (countNominee + 1);
				FileUpload f = fileModel.getNomFile();
				String ext = f.fileName().substring(f.fileName().indexOf("."), f.fileName().length());
				String fileName = nomineeId + EkycConstants.UNDERSCORE + EkycConstants.NOM_PROOF
						+ ext;
				String filePath = props.getFileBasePath() + fileModel.getApplicationId() +slash+ fileName;
				Path path = Paths.get(filePath);
				if (Files.exists(path)) {
					Files.delete(path);
				}
				Files.copy(fileModel.getNomFile().filePath(), path);
				saveDoc(fileModel.getApplicationId(),fileName, filePath, nomineeId + EkycConstants.UNDERSCORE + EkycConstants.NOM_PROOF);
				responseModel = saveNomineeDetails(fileModel, filePath);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.NOM_FILE_NULL);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(fileModel.getApplicationId(),"NomineeService","uploadDocNominee",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In uploadDocNominee for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to upload Guardian Document
	 * 
	 * @author prade
	 * @param fileModel
	 * @param NomineID
	 * @return
	 */
	public String uploadDocGuardian(NomineeDocModel fileModel, String NomineID) {
		String fileUrl = "";
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			File dir = new File(props.getFileBasePath() + fileModel.getApplicationId());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileUpload f = fileModel.getGuardFile();
			String ext = f.fileName().substring(f.fileName().indexOf("."), f.fileName().length());
			String fileName = NomineID + EkycConstants.UNDERSCORE + EkycConstants.GUARDINA_PROOF
					 + ext;
			String filePath = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
			Path path = Paths.get(filePath);
			if (Files.exists(path)) {
				Files.delete(path);
			}
			Files.copy(fileModel.getGuardFile().filePath(), path);
			saveDoc(fileModel.getApplicationId(),fileName, filePath, NomineID + EkycConstants.UNDERSCORE + EkycConstants.GUARDINA_PROOF);
			fileUrl = filePath;
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(fileModel.getApplicationId(),"NomineeService","uploadDocGuardian",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In uploadDocGuardian for the Error: " + e.getMessage(),"ERR-001");
		}
		return fileUrl;
	}

	/**
	 * Method to save and upload Nominee Proof
	 */
	public ResponseModel saveNomineeDetails(NomineeDocModel nomineeEntity, String filePath) {
		ResponseModel responseModel = new ResponseModel();
		NomineeEntity savingNominee = null;
		NomineeEntity entity = null;
		try {
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(nomineeEntity.getApplicationId());
			if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0) {
				Long countNominee = nomineeRepository.countByApplicationId(nomineeEntity.getApplicationId());
				if (countNominee <= 2) {
					ObjectMapper mapper = new ObjectMapper();
					entity = mapper.readValue(nomineeEntity.getNomineeDetails(), NomineeEntity.class);
					entity.setNomineeId("Nominee_" + (countNominee + 1));
					entity.setAttachementUrl(filePath);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					LocalDate localDate = LocalDate.parse(entity.getDateOfbirth(), formatter);
					LocalDate today = LocalDate.now();
					Period p = Period.between(localDate, today);
					if (entity.getId() != null && entity.getId() > 0 && entity.getGuardianEntity() == null) {
						GuardianEntity oldGuardianEntity = guardianRepository.findByNomineeId(entity.getId());
						if (oldGuardianEntity != null) {
							guardianRepository.deleteById(oldGuardianEntity.getId());
						}
					}
					if (p.getYears() >= 18) {
						savingNominee = nomineeRepository.save(entity);
					} else if (p.getYears() < 18 && entity.getGuardianEntity() != null) {
						if (nomineeEntity.getGuardFile() != null
								&& StringUtil.isNotNullOrEmpty(nomineeEntity.getGuardFile().contentType())) {
							savingNominee = nomineeRepository.save(entity);
							String guardFilePath = uploadDocGuardian(nomineeEntity, savingNominee.getNomineeId());
							GuardianEntity guardian = entity.getGuardianEntity();
							guardian.setNomineeId(savingNominee.getId());
							guardian.setAttachementUrl(guardFilePath);
							guardianRepository.save(guardian);
						} else {
							return commonMethods.constructFailedMsg(MessageConstants.GUARD_FILE_NULL);
						}
					} else {
						return commonMethods.constructFailedMsg(MessageConstants.GUARDIAN_REQUIRED);
					}
				} else {
					return commonMethods.constructFailedMsg(MessageConstants.NOMINEE_COUNT);
				}
				if (savingNominee != null) {
					responseModel = allocationForNomiee(savingNominee.getApplicationId(), savingNominee.getId());
				} else {
					responseModel = commonMethods
							.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_NOMINEE_DETAILS);
				}
			} else {
				if (user.isEmpty()) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
				}
			}
			rejectionStatusHelper.insertArchiveTableRecord(nomineeEntity.getApplicationId(),
					EkycConstants.PAGE_NOMINEE);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(nomineeEntity.getApplicationId(),"NomineeService","saveNomineeDetails",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In saveNomineeDetails for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
	
	
	 public void saveDoc(long applicationId,String fileName, String outputPath, String imageName) {
    	 DocumentEntity documentEntity=new DocumentEntity();
    	 	DocumentEntity oldRecord = docrepository.findByApplicationIdAndDocumentType(applicationId,
    	 			imageName);
    	 	if (oldRecord!=null) {
    	 		documentEntity.setId(oldRecord.getId());
    	 		documentEntity=oldRecord;
    	 	}
    	 	documentEntity.setAttachementUrl(outputPath);
    	 	documentEntity.setDocumentType(imageName);
    	 	documentEntity.setAttachement(fileName);
    	 	documentEntity.setApplicationId(applicationId);
    	 	documentEntity.setTypeOfProof(imageName);
    	 	docrepository.save(documentEntity);
	}

	/**
	 * Method to save allocation for nominee
	 * 
	 * @param ApplicationId
	 * @param id
	 * @author prade
	 */
	public ResponseModel allocationForNomiee(Long ApplicationId, Long id) {
		ResponseModel responseModel = new ResponseModel();
		try {
		responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		List<NomineeEntity> nomineeEntities = nomineeRepository.findByapplicationId(ApplicationId);
		Collections.sort(nomineeEntities, new Comparator<NomineeEntity>() {
			public int compare(NomineeEntity e1, NomineeEntity e2) {
				return Integer.compare(Math.toIntExact(e1.getId()), Math.toIntExact(e2.getId()));
			}
		});
		if (nomineeEntities.size() == 1) {
			for (NomineeEntity neList : nomineeEntities) {
				neList.setAllocation(100);
			}
			nomineeRepository.saveAll(nomineeEntities);
			commonMethods.UpdateStep(EkycConstants.PAGE_NOMINEE_1, ApplicationId);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE_2);
		} else if (nomineeEntities.size() == 2) {
			for (NomineeEntity neList : nomineeEntities) {
				neList.setAllocation(50);
			}
			nomineeRepository.saveAll(nomineeEntities);
			commonMethods.UpdateStep(EkycConstants.PAGE_NOMINEE_2, ApplicationId);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE_3);
		} else if (nomineeEntities.size() == 3) {
			int count = 1;
			for (NomineeEntity neList : nomineeEntities) {
				if (count == 1) {
					neList.setAllocation(34);
				} else {
					neList.setAllocation(33);
				}
				count++;
			}
			nomineeRepository.saveAll(nomineeEntities);
			commonMethods.UpdateStep(EkycConstants.PAGE_NOMINEE_3, ApplicationId);
			responseModel.setPage(EkycConstants.PAGE_DOCUMENT);
		}
		List<NomineeEntity> updatedNomineeList = nomineeRepository.findByapplicationId(ApplicationId);
		responseModel.setResult(updatedNomineeList);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(ApplicationId,"NomineeService","allocationForNomiee",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In allocationForNomiee for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to calculate Nominee Allocation percent
	 * 
	 * @param entity
	 * @param countNominee
	 * @return
	 */
	private int calculateNomineeAllocation(NomineeEntity entity, int countNominee) {
		int allocationTally = 0;
		try {
		if (countNominee == 1) {
			allocationTally = 100;
		} else if (countNominee == 2 && entity.getNomOneAllocation() > 0 && entity.getNomTwoAllocation() > 0) {
			allocationTally = entity.getNomOneAllocation() + entity.getNomTwoAllocation();
		} else if (countNominee == 3 && entity.getNomOneAllocation() > 0 && entity.getNomTwoAllocation() > 0
				&& entity.getNomThreeAllocation() > 0) {
			allocationTally = entity.getNomOneAllocation() + entity.getNomTwoAllocation()
					+ entity.getNomThreeAllocation();
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(entity.getApplicationId(),"NomineeService","calculateNomineeAllocation",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In calculateNomineeAllocation for the Error: " + e.getMessage(),"ERR-001");
		}
		return allocationTally;
	}

	/**
	 * Method to save allocation
	 * 
	 * @param ApplicationId
	 * @param id
	 * @param entity
	 * @author prade
	 */
	public ResponseModel updateNomineeAllocation(NomineeEntity entity) {
		ResponseModel responseModel = new ResponseModel();
		try {
		responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		List<NomineeEntity> nomineeEntities = nomineeRepository.findByapplicationId(entity.getApplicationId());
		Collections.sort(nomineeEntities, new Comparator<NomineeEntity>() {
			public int compare(NomineeEntity e1, NomineeEntity e2) {
				return Integer.compare(Math.toIntExact(e1.getId()), Math.toIntExact(e2.getId()));
			}
		});
		int allocataionTally = calculateNomineeAllocation(entity, nomineeEntities.size());
		if (allocataionTally == 100) {
			if (nomineeEntities.size() == 1) {
				for (NomineeEntity neList : nomineeEntities) {
					neList.setAllocation(100);
				}
			} else if (nomineeEntities.size() == 2) {
				int count = 1;
				for (NomineeEntity neList : nomineeEntities) {
					if (count == 1) {
						neList.setAllocation(entity.getNomOneAllocation());
					} else {
						neList.setAllocation(entity.getNomTwoAllocation());
					}
					count++;
				}
			} else if (nomineeEntities.size() == 3) {
				int count = 1;
				for (NomineeEntity neList : nomineeEntities) {
					if (count == 1) {
						neList.setAllocation(entity.getNomOneAllocation());
					} else if (count == 2) {
						neList.setAllocation(entity.getNomTwoAllocation());
					} else {
						neList.setAllocation(entity.getNomThreeAllocation());
					}
					count++;
				}
			}
			nomineeRepository.saveAll(nomineeEntities);
			commonMethods.UpdateStep(EkycConstants.PAGE_NOMINEE_3, entity.getApplicationId());
			responseModel.setResult(nomineeEntities);
			responseModel.setPage(EkycConstants.PAGE_DOCUMENT);
		} else {
			return commonMethods.constructFailedMsg(MessageConstants.ALLOCATION_NOT_TALLY);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(entity.getApplicationId(),"NomineeService","updateNomineeAllocation",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In updateNomineeAllocation for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to delete Nominee
	 */
	@Override
	public ResponseModel deleteNom(long id) {
		ResponseModel responseModel = new ResponseModel();
		try {
		Optional<NomineeEntity> nominee = nomineeRepository.findById(id);
		if (nominee.isPresent()) {
			GuardianEntity savedGuardianEntity = guardianRepository.findByNomineeId(id);
			if (savedGuardianEntity != null && savedGuardianEntity.getId() > 0) {
				guardianRepository.deleteById(savedGuardianEntity.getId());
			}
			nomineeRepository.deleteById(id);
			updateAllocaionAfterDelete(nominee.get().getApplicationId());
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(id,"NomineeService","deleteNom",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In deleteNom for the Error: " + e.getMessage(),"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to update nominee Allocation
	 */
	public void updateAllocaionAfterDelete(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		List<NomineeEntity> nomineeEntities = nomineeRepository.findByapplicationId(applicationId);
		if (nomineeEntities.size() == 1) {
			for (NomineeEntity neList : nomineeEntities) {
				neList.setAllocation(100);
			}
		} else if (nomineeEntities.size() == 2) {
			for (NomineeEntity neList : nomineeEntities) {
				neList.setAllocation(50);
			}
			responseModel.setResult(nomineeEntities);
		}
		nomineeRepository.saveAll(nomineeEntities);
	
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		commonMethods.SaveLog(applicationId,"NomineeService","updateAllocaionAfterDelete",e.getMessage());
		commonMethods.sendErrorMail("An error occurred while processing your request, In updateAllocaionAfterDelete for the Error: " + e.getMessage(),"ERR-001");
		responseModel = commonMethods.constructFailedMsg(e.getMessage());
	}
	}

}

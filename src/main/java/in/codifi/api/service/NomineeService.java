package in.codifi.api.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.GuardianEntity;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.model.NomineeDocModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.GuardianRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.service.spec.INomineeService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Service
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

//	/**
//	 * Method to save Nominee Details
//	 */
//
//	@Override
//	public ResponseModel saveNominee(NomineeEntity nomineeEntity) {
//		ResponseModel responseModel = new ResponseModel();
//		Long countNominee = nomineeRepository.countByApplicationId(nomineeEntity.getApplicationId());
//		if (countNominee <= 2) {
//			if (nomineeEntity.getApplicationId() != null) {
//				Optional<ApplicationUserEntity> user = applicationUserRepository
//						.findById(nomineeEntity.getApplicationId());
//				if (user.isPresent() && user.get().getSmsVerified() > 0 && user.get().getEmailVerified() > 0) {
//					NomineeEntity savingNominee = nomineeRepository.save(nomineeEntity);
//					commonMethods.UpdateStep(8, nomineeEntity.getApplicationId());
//					responseModel.setResult(savingNominee);
//					LocalDate givenDate = savingNominee.getDateofbirth();
//					LocalDate today = LocalDate.now();
//					Period p = Period.between(givenDate, today);
//					System.out.print("The years is " + p.getYears());
//					if (p.getYears() <= 18) {
//						nomineeEntity.getGuardianEntity().setNomineeId(savingNominee.getId());
//						guardianRepository.save(nomineeEntity.getGuardianEntity());
//					} else if (nomineeEntity.getGuardianEntity() != null) {
//						responseModel = commonMethods.constructFailedMsg(MessageConstants.GUARDIAN_MSG);
//					}
//				} else {
//					if (user.isEmpty()) {
//						responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
//					} else {
//						responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_NOT_VERIFIED);
//					}
//				}
//				if (responseModel != null) {
//					commonMethods.UpdateStep(8, nomineeEntity.getApplicationId());
//					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
//					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
//					responseModel.setPage(EkycConstants.PAGE_NOMINEE);
//				} else {
//					responseModel = commonMethods
//							.constructFailedMsg(MessageConstants.ERROR_WHILE_SAVING_NOMINEE_DETAILS);
//				}
//			} else {
//				responseModel = commonMethods.constructFailedMsg(MessageConstants.NOMINEE_AVAILABLE);
//			}
//		} else {
//			responseModel = commonMethods.constructFailedMsg(MessageConstants.NOMINEE_COUNT);
//		}
//		return responseModel;
//	}
//
	/**
	 * Method to get Nominee Details
	 **/
	@Override
	public ResponseModel getNominee(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		List<NomineeEntity> savedEntity = populateNomineeAndGuardian(applicationId);
		if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(savedEntity);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
		}
		return responseModel;
	}

	/**
	 * Method to populate nominee and Guardian Details
	 * 
	 * @param applicationId
	 * @return
	 */
	public List<NomineeEntity> populateNomineeAndGuardian(long applicationId) {
		List<NomineeEntity> savedEntity = nomineeRepository.findByapplicationId(applicationId);
		if (StringUtil.isListNotNullOrEmpty(savedEntity)) {
			savedEntity.forEach(entity -> {
				entity.setGuardianEntity(guardianRepository.findByNomineeId(entity.getId()));
			});
		}
		return savedEntity;
	}

//	/**
//	 * Method to upload nominee Proof
//	 * 
//	 * @param fileModel (file,TypeofProof,ApplicationId)
//	 * @return
//	 */
//	@Override
//	public ResponseModel uploadDocNominee(NomineeDocModel fileModel) {
//		ResponseModel responseModel = new ResponseModel();
//		try {
//			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
//			if (OS.contains(EkycConstants.OS_WINDOWS)) {
//				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
//			}
//			File dir = new File(props.getFileBasePath() + fileModel.getId());
//			if (!dir.exists()) {
//				dir.mkdirs();
//			}
//
//			NomineeEntity updatedDocEntity = null;
//			Optional<NomineeEntity> savedEntity = nomineeRepository.findById(fileModel.getId());
//			if (savedEntity.isPresent()) {
//				FileUpload f = fileModel.getFile();
//				String ext = f.fileName().substring(f.fileName().indexOf("."), f.fileName().length());
//				String fileName = fileModel.getId() + EkycConstants.UNDERSCORE + fileModel.getTypeOfProof() + ext;
//				String filePath = props.getFileBasePath() + fileModel.getId() + slash + fileName;
//				Path path = Paths.get(filePath);
//				if (Files.exists(path)) {
//					Files.delete(path);
//				}
//
//				NomineeEntity isPresend = savedEntity.get();
//				isPresend.setAttachementUrl(filePath);
//				updatedDocEntity = nomineeRepository.save(isPresend);
//				Files.copy(fileModel.getFile().filePath(), path);
//				;
//			} else {
//				responseModel.setMessage(EkycConstants.FAILED_MSG);
//				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
//			}
//			if (updatedDocEntity != null) {
//				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
//				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
//				responseModel.setResult(updatedDocEntity);
//				responseModel.setPage(EkycConstants.PAGE_NOMINEE);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return responseModel;
//	}

	/**
	 * Method to upload nominee details
	 * 
	 * @param fileModel
	 * @return
	 */
	@Override
	public ResponseModel uploadDocNominee(NomineeDocModel fileModel) {
		ResponseModel responseModel = new ResponseModel();
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			File dir = new File(props.getFileBasePath() + fileModel.getApplicationId());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileUpload f = fileModel.getFile();
			String ext = f.fileName().substring(f.fileName().indexOf("."), f.fileName().length());
			String fileName = fileModel.getApplicationId() + EkycConstants.UNDERSCORE + EkycConstants.NOM_PROOF + ext;
			String filePath = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
			Path path = Paths.get(filePath);
			if (Files.exists(path)) {
				Files.delete(path);
			}
			Files.copy(fileModel.getFile().filePath(), path);
			;
			responseModel.setResult(saveNomineeDetails(fileModel, filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;
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
					int allocataionTally = calculateNomineeAllocation(entity, countNominee);
					if (allocataionTally != 100) {
						return commonMethods.constructFailedMsg(MessageConstants.ALLOCATION_NOT_TALLY);
					} else {
						if (p.getYears() > 19 && entity.getGuardianEntity() == null) {
							savingNominee = nomineeRepository.save(entity);
						} else if (p.getYears() < 19 && entity.getGuardianEntity() != null) {
							savingNominee = nomineeRepository.save(entity);
							GuardianEntity guardian = entity.getGuardianEntity();
							guardian.setNomineeId(savingNominee.getId());
							guardianRepository.save(guardian);
						} else {
							return commonMethods.constructFailedMsg(MessageConstants.GUARDIAN_REQUIRED);
						}
					}
				} else {
					return commonMethods.constructFailedMsg(MessageConstants.NOMINEE_COUNT);
				}
				if (savingNominee != null) {
					responseModel = Stageandallocate(savingNominee.getApplicationId(), savingNominee.getId(), entity);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;
	}

	private int calculateNomineeAllocation(NomineeEntity entity, Long countNominee) {
		int allocationTally = 0;
		if (countNominee == 0) {
			allocationTally = 100;
		} else if (countNominee == 1 && entity.getNomOneAllocation() > 0 && entity.getNomTwoAllocation() > 0) {
			allocationTally = entity.getNomOneAllocation() + entity.getNomTwoAllocation();
		} else if (countNominee == 2 && entity.getNomOneAllocation() > 0 && entity.getNomTwoAllocation() > 0
				&& entity.getNomThreeAllocation() > 0) {
			allocationTally = entity.getNomOneAllocation() + entity.getNomTwoAllocation()
					+ entity.getNomThreeAllocation();
		}
		return allocationTally;
	}

	/**
	 * Method to save allocation
	 * 
	 * @param ApplicationId
	 * @param id
	 * @param entity
	 */
	public ResponseModel Stageandallocate(Long ApplicationId, Long id, NomineeEntity entity) {
		ResponseModel responseModel = new ResponseModel();
		responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		Long countNominee = nomineeRepository.countByApplicationId(ApplicationId);
		if (countNominee == 1) {
			Optional<NomineeEntity> exeentity = nomineeRepository.findById(id);
			if (exeentity.isPresent()) {
				NomineeEntity isuserPresend = exeentity.get();
				isuserPresend.setAllocation(100);
				nomineeRepository.save(isuserPresend);
				responseModel.setResult(isuserPresend);
			}
			commonMethods.UpdateStep(8.1, ApplicationId);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE_2);
		} else if (countNominee == 2) {
			List<NomineeEntity> nomineeEntities = nomineeRepository.findByapplicationId(ApplicationId);
			for (NomineeEntity neList : nomineeEntities) {
				if (StringUtil.isEqual(neList.getNomineeId(), "Nominee_1")) {
					neList.setAllocation(entity.getNomOneAllocation());
				} else {
					neList.setAllocation(entity.getNomTwoAllocation());
				}
			}
			nomineeRepository.saveAll(nomineeEntities);
			commonMethods.UpdateStep(8.2, ApplicationId);
			responseModel.setResult(nomineeEntities);
			responseModel.setPage(EkycConstants.PAGE_NOMINEE_3);
		} else if (countNominee == 3) {
			List<NomineeEntity> nomineeEntities = nomineeRepository.findByapplicationId(ApplicationId);
			for (NomineeEntity neList : nomineeEntities) {
				if (StringUtil.isEqual(neList.getNomineeId(), "Nominee_1")) {
					neList.setAllocation(entity.getNomOneAllocation());
				} else if (StringUtil.isEqual(neList.getNomineeId(), "Nominee_2")) {
					neList.setAllocation(entity.getNomTwoAllocation());
				} else {
					neList.setAllocation(entity.getNomThreeAllocation());
				}
			}
			nomineeRepository.saveAll(nomineeEntities);
			commonMethods.UpdateStep(8.3, ApplicationId);
			responseModel.setResult(nomineeEntities);
			responseModel.setPage(EkycConstants.PAGE_DOCUMENT);
		}
		return responseModel;
	}

}

package in.codifi.api.service;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.helper.DocumentHelper;
import in.codifi.api.model.DocumentCheckModel;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.service.spec.IDocumentService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class DocumentService implements IDocumentService {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	DocumentRepository docrepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationProperties props;
	@Inject
	DocumentHelper documentHelper;
	@Inject
	ApplicationUserRepository userRepository;

	private static final Logger logger = LogManager.getLogger(DocumentService.class);
	/**
	 * Method to upload file
	 */
	@Override
	public ResponseModel uploadDoc(FormDataModel fileModel) {
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
			if (fileModel.getApplicationId() != 0 && fileModel.getApplicationId() > 0 && fileModel.getFile() != null
					&& StringUtil.isNotNullOrEmpty(fileModel.getFile().contentType())) {
				boolean content = (fileModel.getFile().contentType().equals(EkycConstants.CONST_APPLICATION_PDF));
				if (content) {
					String fileName = fileModel.getApplicationId() + EkycConstants.UNDERSCORE
							+ fileModel.getTypeOfProof() + EkycConstants.PDF_EXTENSION;
					String totalFileName = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
					Path path = fileModel.getFile().filePath();
					String errorMsg = checkPasswordProtected(fileModel);
					if (StringUtil.isNullOrEmpty(errorMsg)) {
						PDDocument document = PDDocument.load(new File(path.toString()), fileModel.getPassword());
						document.getClass();
						if (document.isEncrypted()) {
							document.setAllSecurityToBeRemoved(true);
							document.save(totalFileName);
							document.close();
							responseModel = saveDoc(fileModel, fileName, totalFileName);
						} else {
							document.save(totalFileName);
							document.close();
							responseModel = saveDoc(fileModel, fileName, totalFileName);
						}
					} else {
						return commonMethods.constructFailedMsg(errorMsg);
					}
				} else if (!content) {
					String errorMsg = checkValidate(fileModel);
					if (StringUtil.isNullOrEmpty(errorMsg)) {
						FileUpload f = fileModel.getFile();
						String ext = f.fileName().substring(f.fileName().indexOf("."), f.fileName().length());
						String fileName = fileModel.getApplicationId() + EkycConstants.UNDERSCORE
								+ fileModel.getTypeOfProof() + ext;
						String filePath = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
						Path path = Paths.get(filePath);
						if (Files.exists(path)) {
							Files.delete(path);
						}
						Files.copy(fileModel.getFile().filePath(), path);
						responseModel = saveDoc(fileModel, fileName, filePath);
					} else {
						return commonMethods.constructFailedMsg(errorMsg);
					}
				} else {
					responseModel.setMessage(EkycConstants.FAILED_MSG);
				}
			} else {
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				if (fileModel.getFile() == null || StringUtil.isNullOrEmpty(fileModel.getFile().contentType())) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.FILE_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to save Document
	 * 
	 * @param data
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	public ResponseModel saveDoc(FormDataModel data, String fileName, String filePath) {
		ResponseModel responseModel = new ResponseModel();
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			DocumentEntity updatedDocEntity = null;
			DocumentEntity oldRecord = docrepository.findByApplicationIdAndDocumentType(data.getApplicationId(),
					data.getTypeOfProof());
			if (oldRecord != null) {
				oldRecord.setAttachement(fileName);
				oldRecord.setDocumentType(data.getDocumentType());
				if (StringUtil.isNotNullOrEmpty(data.getTypeOfProof())) {
					oldRecord.setTypeOfProof(data.getTypeOfProof());
				}

				oldRecord.setAttachementUrl(props.getImageUrlPath() + data.getApplicationId() + slash + fileName);
				oldRecord.setPassword(data.getPassword());
				updatedDocEntity = docrepository.save(oldRecord);
			} else {
				DocumentEntity doc = new DocumentEntity();
				doc.setApplicationId(data.getApplicationId());
				doc.setDocumentType(data.getDocumentType());
				doc.setAttachement(fileName);
				if (StringUtil.isNotNullOrEmpty(data.getTypeOfProof())) {
					doc.setTypeOfProof(data.getTypeOfProof());
				}
				doc.setAttachementUrl(props.getImageUrlPath() + data.getApplicationId() + slash + fileName);
				doc.setPassword(data.getPassword());
				updatedDocEntity = docrepository.save(doc);
			}
			if (updatedDocEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setResult(updatedDocEntity);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.FAILED_DOC_UPLOAD);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to check file Validation
	 * 
	 * @param data
	 * @return
	 */
	public String checkValidate(FormDataModel data) {
		List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg", "image/gif", "image/png");
		if (!mimetype.contains(data.getFile().contentType())) {
			return "File not suported";
		}
		return "";
	}

	/**
	 * Method to check file is password protected
	 * 
	 * @param fileModel
	 * @return
	 */
	public String checkPasswordProtected(FormDataModel fileModel) {
		String error = "";
		try {
			Path path = fileModel.getFile().filePath();
			PDDocument document = PDDocument.load(new File(path.toString()), fileModel.getPassword());
			if (document.isEncrypted()) {
				document.setAllSecurityToBeRemoved(true);
				document.close();
				error = "";
			} else {
				error = "";
			}
		} catch (InvalidPasswordException e) {
			logger.error("invalid Password " + e.getMessage());
			error = "invalid Password";
		} catch (IOException e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return error;
	}

	/**
	 * Method to check document present or not
	 */
	@Override
	public ResponseModel getDocument(@NotNull long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		DocumentCheckModel documentCheckModel = null;
		try {
			List<DocumentEntity> documents = docrepository.findByApplicationId(applicationId);
			if (StringUtil.isListNotNullOrEmpty(documents)) {
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				documentCheckModel = new DocumentCheckModel();
				for (DocumentEntity docEntity : documents) {
					if (docEntity != null && StringUtil.isNotNullOrEmpty(docEntity.getDocumentType())) {
						if (StringUtil.isEqual(EkycConstants.DOC_IVR, docEntity.getDocumentType())) {
							documentCheckModel.setIpvPresent(true);
							documentCheckModel.setIpvUrl(docEntity.getAttachementUrl());
						} else if (StringUtil.isEqual(EkycConstants.DOC_CHEQUE, docEntity.getDocumentType())) {
							documentCheckModel.setCancelledChequeOrStatement(true);
							documentCheckModel.setChecqueName(docEntity.getAttachement());
						} else if (StringUtil.isEqual(EkycConstants.DOC_INCOME, docEntity.getDocumentType())) {
							documentCheckModel.setIncomeProofPresent(true);
							documentCheckModel.setIncomeProofName(docEntity.getAttachement());
							documentCheckModel.setIncomeProofType(docEntity.getTypeOfProof());
						} else if (StringUtil.isEqual(EkycConstants.DOC_SIGNATURE, docEntity.getDocumentType())) {
							documentCheckModel.setSignaturePresent(true);
							documentCheckModel.setSignName(docEntity.getAttachement());
						} else if (StringUtil.isEqual(EkycConstants.DOC_PAN, docEntity.getDocumentType())) {
							documentCheckModel.setPanName(docEntity.getAttachement());
							documentCheckModel.setPanCardPresent(true);
						}
					}
				}
				responseModel.setResult(documentCheckModel);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.NOT_FOUND_DATA);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return responseModel;
	}

	/**
	 * Method to delete uploaded documents
	 */
	@Override
	public ResponseModel deleteDocument(@NotNull long applicationId, @NotNull String type) {
		ResponseModel responseModel = new ResponseModel();
		responseModel.setPage(EkycConstants.PAGE_DOCUMENT);
		try {
			DocumentEntity documents = docrepository.findByApplicationIdAndDocumentType(applicationId, type);
			if (documents != null) {
				docrepository.delete(documents);
				Optional<ApplicationUserEntity> checkApplicationID = userRepository.findById(applicationId);
				ApplicationUserEntity oldUserEntity = checkApplicationID.get();
				oldUserEntity.setStage(EkycConstants.PAGE_NOMINEE_3);
				userRepository.save(oldUserEntity);

				responseModel.setMessage("Document deleted successfully");
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			} else {
				responseModel.setStat(EkycConstants.FAILED_STATUS);
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				responseModel.setMessage("Document not found");
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			responseModel.setReason("Error deleting document");
		}
		return responseModel;
	}

	/**
	 * Method to download file
	 */
	@Override
	public Response downloadFile(@NotNull long applicationId, @NotNull String type) {
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}

			DocumentEntity documents = docrepository.findByApplicationIdAndDocumentType(applicationId, type);
			if (documents != null && StringUtil.isNotNullOrEmpty(documents.getAttachement())) {
				String path = props.getFileBasePath() + applicationId + slash + documents.getAttachement();
				String contentType = URLConnection.guessContentTypeFromName(documents.getAttachement());
				File file = new File(path);
				ResponseBuilder response = Response.ok((Object) file);
				response.type(contentType);
				response.header("Content-Disposition", "attachment;filename=" + file.getName());
				return response.build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.FILE_NOT_FOUND)
						.build();
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to download file: " + e.getMessage()).build();
		}
	}

	/**
	 * Method to confirm document
	 */
	@Override
	public ResponseModel confirmDocument(@NotNull long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		try {
		Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
		if (isUserPresent.isPresent()) {
			commonMethods.UpdateStep(EkycConstants.PAGE_DOCUMENT, applicationId);
			responseModel.setPage(EkycConstants.PAGE_IPV);
			responseModel.setReason("Document Confirmed successfully");
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		} else {
			responseModel.setPage(EkycConstants.PAGE_DOCUMENT);
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			responseModel.setReason(MessageConstants.USER_ID_INVALID);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return responseModel;
	}

}

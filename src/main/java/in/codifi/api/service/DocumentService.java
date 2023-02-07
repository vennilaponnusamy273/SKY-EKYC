package in.codifi.api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.springframework.stereotype.Service;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.service.spec.IDocumentService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Service
public class DocumentService implements IDocumentService {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	DocumentRepository docrepository;

	@Inject
	CommonMethods commonMethods;

	@Inject
	ApplicationProperties props;

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
			if (fileModel.getApplicationId() != 0 && fileModel.getApplicationId() > 0) {
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
							commonMethods.UpdateStep(10, fileModel.getApplicationId());
						} else {
							document.save(totalFileName);
							document.close();
							responseModel = saveDoc(fileModel, fileName, totalFileName);
							commonMethods.UpdateStep(10, fileModel.getApplicationId());
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
						commonMethods.UpdateStep(10, fileModel.getApplicationId());
					} else {
						return commonMethods.constructFailedMsg(errorMsg);
					}
				} else {
					responseModel.setMessage(EkycConstants.FAILED_MSG);
				}
			} else {
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;
	}

	public ResponseModel saveDoc(FormDataModel data, String fileName, String filePath) {
		ResponseModel responseModel = new ResponseModel();
		try {
			DocumentEntity updatedDocEntity = null;
			DocumentEntity oldRecord = docrepository.findByApplicationIdAndTypeOfProof(data.getApplicationId(),
					data.getTypeOfProof());
			if (oldRecord != null) {
				oldRecord.setAttachement(data.getFile().contentType());
				oldRecord.setTypeOfProof(data.getTypeOfProof());
				oldRecord.setAttachementUrl(filePath);
				oldRecord.setPassword(data.getPassword());
				updatedDocEntity = docrepository.save(oldRecord);
			} else {
				DocumentEntity doc = new DocumentEntity();
				doc.setApplicationId(data.getApplicationId());
				doc.setAttachement(data.getFile().contentType());
				doc.setTypeOfProof(data.getTypeOfProof());
				doc.setAttachementUrl(filePath);
				doc.setPassword(data.getPassword());
				updatedDocEntity = docrepository.save(doc);
			}
			if (updatedDocEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setResult(updatedDocEntity);
			} else {
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				responseModel = commonMethods.constructFailedMsg(MessageConstants.FAILED_DOC_UPLOAD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;
	}

	public String checkValidate(FormDataModel data) {
		List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg", "image/gif", "image/png");
		if (!mimetype.contains(data.getFile().contentType())) {
			return "File not suported";
		}
		if (data.getFile().size() > 1024 * 1024 * 4) {
			return "File much large";
		}
		return "";

	}

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
			error = "invalid Password";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return error;
	}

}

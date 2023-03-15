package in.codifi.api.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.helper.DocumentHelper;
import in.codifi.api.model.DocumentCheckModel;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.IvrModel;
import in.codifi.api.model.LivenessCheckReqModel;
import in.codifi.api.model.LivenessCheckResModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.restservice.AryaLivenessCheck;
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
	AryaLivenessCheck aryaLivenessCheck;

	@Inject
	DocumentHelper documentHelper;

	@Inject
	ApplicationUserRepository userRepository;

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
							commonMethods.UpdateStep(9, fileModel.getApplicationId());
						} else {
							document.save(totalFileName);
							document.close();
							responseModel = saveDoc(fileModel, fileName, totalFileName);
							commonMethods.UpdateStep(9, fileModel.getApplicationId());
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
						commonMethods.UpdateStep(9, fileModel.getApplicationId());
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
			e.printStackTrace();
		}
		return responseModel;
	}

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

	/**
	 * Method to upload IVR Document
	 */
	@Override
	public ResponseModel uploadIvr(IvrModel ivrModel) {
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		ResponseModel responseModel = new ResponseModel();
		try {
			List<String> errorList = checkIvrModel(ivrModel);
			if (StringUtil.isListNullOrEmpty(errorList)) {
				LivenessCheckReqModel reqModel = new LivenessCheckReqModel();
				reqModel.setDoc_base64(ivrModel.getImageUrl());
				reqModel.setReq_id(ivrModel.getApplicationId());
				LivenessCheckResModel model = aryaLivenessCheck.livenessCheck(reqModel);
				ObjectMapper mapper = new ObjectMapper();
				System.out.println(mapper.writeValueAsString(model));
				if (model != null && model.getDocJson() != null
						&& Double.parseDouble(model.getDocJson().getReal()) >= 0.50) {
					String ivrName = documentHelper.convertBase64ToImage(ivrModel.getImageUrl(),
							ivrModel.getApplicationId());
					DocumentEntity oldRecord = docrepository
							.findByApplicationIdAndDocumentType(ivrModel.getApplicationId(), EkycConstants.DOC_IVR);
					DocumentEntity updatedDocEntity = null;
					if (oldRecord != null) {
						oldRecord.setAttachement(ivrName);
						oldRecord.setDocumentType(EkycConstants.DOC_IVR);
						oldRecord.setTypeOfProof(EkycConstants.DOC_IVR);
						oldRecord.setAttachementUrl(
								props.getImageUrlPath() + ivrModel.getApplicationId() + slash + ivrName);
						oldRecord.setLatitude(ivrModel.getLatitude());
						oldRecord.setLongitude(ivrModel.getLongitude());
						updatedDocEntity = docrepository.save(oldRecord);
					} else {
						DocumentEntity doc = new DocumentEntity();
						doc.setApplicationId(ivrModel.getApplicationId());
						doc.setAttachement(ivrName);
						doc.setTypeOfProof(EkycConstants.DOC_IVR);
						doc.setDocumentType(EkycConstants.DOC_IVR);
						doc.setAttachementUrl(props.getImageUrlPath() + ivrModel.getApplicationId() + slash + ivrName);
						doc.setLatitude(ivrModel.getLatitude());
						doc.setLongitude(ivrModel.getLongitude());
						updatedDocEntity = docrepository.save(doc);
					}
					if (updatedDocEntity != null) {
						commonMethods.UpdateStep(10, ivrModel.getApplicationId());
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setResult(updatedDocEntity);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.FAILED_IVR_DOC_UPLOAD);
					}
				} else {
					if (model != null && model.getDocJson() != null
							&& Double.parseDouble(model.getDocJson().getSpoof()) > 0.01) {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_IVR_INVALID);
						responseModel.setReason(model.getErrorMessage() + model.getDocJson().getSpoof());
						responseModel.setResult(model);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_LIVENESS);
					}
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_IVR_PARAMS);
				responseModel.setResult(errorList);
			}
		} catch (Exception e) {
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
			e.printStackTrace();
		}
		return responseModel;
	}

	public List<String> checkIvrModel(IvrModel ivrModel) {
		List<String> errorList = new ArrayList<>();
		if (StringUtil.isNullOrEmpty(ivrModel.getImageUrl())) {
			errorList.add(MessageConstants.IVR_IMAGE_NULL);
		}
		if (StringUtil.isNullOrEmpty(ivrModel.getLatitude())) {
			errorList.add(MessageConstants.IVR_LAT_NULL);
		}
		if (StringUtil.isNullOrEmpty(ivrModel.getLongitude())) {
			errorList.add(MessageConstants.IVR_LON_NULL);
		}
		return errorList;
	}

	/**
	 * Method to generate IVR Link
	 */
	@Override
	public ResponseModel getLinkIvr(@NotNull long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		Optional<ApplicationUserEntity> isUserPresent = userRepository.findById(applicationId);
		if (isUserPresent.isPresent()) {
			String FirstName = isUserPresent.get().getFirstName();
			String Email = isUserPresent.get().getEmailId();
			Long Mobile_No = isUserPresent.get().getMobileNo();
			String RandomencodedUuid = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(UUID.randomUUID().toString().getBytes());
			String baseUrl = props.getIvrBaseUrl();
			String apiKey = props.getBitlyAccessToken();
			// String url = baseUrl + "?key=" + apiKey + "&short=" + "ivpBaseURl" +
			// ApplicationId + "&name=" + FirstName + "&userDomain=1&randomKey=" +
			// RandomencodedUuid;
			String url = baseUrl + EkycConstants.IVR_KEY + apiKey + EkycConstants.IVR_SHORT + EkycConstants.IVPBASEURL
					+ applicationId + EkycConstants.IVR_NAME + FirstName + EkycConstants.IVR_USER_DOMAIN_AND_RANDOMKEY
					+ RandomencodedUuid;
			try {
				String generateShortLink = generateShortLink(url);
				commonMethods.sendIvrLinktoMobile(generateShortLink, Mobile_No);
				commonMethods.sendMailIvr(generateShortLink, Email);
				JSONObject result = new JSONObject();
				result.put("ivrURL", generateShortLink);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setReason(EkycConstants.SUCCESS_MSG);
				responseModel.setResult(generateShortLink);
			} catch (Exception e) {
				responseModel.setStat(EkycConstants.FAILED_STATUS);
				responseModel.setMessage(EkycConstants.IVR_FAILED_MESSAGE);
				responseModel.setReason(e.getMessage());
				e.printStackTrace();
			}
		}
		return responseModel;
	}

	public String generateShortLink(String longUrl) {
		HttpURLConnection conn = null;
		String shortUrl = "";
		try {
			String apiKey = props.getBitlyAccessToken();
			String apiUrl = String.format(props.getBitlyBaseUrl(), apiKey,
					URLEncoder.encode(longUrl, StandardCharsets.UTF_8));
			URL url = new URL(apiUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_GET);
			conn.setRequestProperty(EkycConstants.IVR_ACCEPT, EkycConstants.CONSTANT_APPLICATION_JSON);

			if (conn.getResponseCode() != 200) {
				BufferedReader errorReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				String errorOutput;
				StringBuilder errorResponseBuilder = new StringBuilder();
				while ((errorOutput = errorReader.readLine()) != null) {
					errorResponseBuilder.append(errorOutput);
				}
				throw new RuntimeException(MessageConstants.FAILED_HTTP_CODE + conn.getResponseCode() + " : "
						+ errorResponseBuilder.toString());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			StringBuilder responseBuilder = new StringBuilder();

			while ((output = in.readLine()) != null) {
				responseBuilder.append(output);
			}
			JSONObject responseJson = new JSONObject(responseBuilder.toString());
			JSONObject urlObj = responseJson.getJSONObject(EkycConstants.URL);
			shortUrl = urlObj.getString(EkycConstants.SHORT_URL);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return shortUrl;
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
			e.printStackTrace();
		}
		return responseModel;
	}

	/**
	 * Method to delete uploaded documents
	 */
	@Override
	public ResponseModel deleteDocument(@NotNull long applicationId, @NotNull String type) {
		ResponseModel responseModel = new ResponseModel();
		try {
			DocumentEntity documents = docrepository.findByApplicationIdAndDocumentType(applicationId, type);
			if (documents != null) {
				docrepository.delete(documents);
				responseModel.setMessage("Document deleted successfully");
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			} else {
				responseModel.setStat(EkycConstants.FAILED_STATUS);
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				responseModel.setMessage("Document not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel.setStat(EkycConstants.FAILED_STATUS);
			responseModel.setMessage(EkycConstants.FAILED_MSG);
			responseModel.setReason("Error deleting document");
		}
		return responseModel;
	}

}

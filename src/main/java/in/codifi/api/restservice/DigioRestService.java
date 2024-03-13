package in.codifi.api.restservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.model.DigioIniResponseModel;
import in.codifi.api.model.DigioRequestModel;
import in.codifi.api.model.DigioSaveAddResponse;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class DigioRestService {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	@RestClient
	IDigioRestService digioRestService;
	@Inject
	ApplicationProperties props;
	@Inject
	ApplicationUserRepository repository;
	@Inject
	DocumentRepository documentRepository;
	private static final Logger logger = LogManager.getLogger(DigioRestService.class);

	/**
	 * Method to check liveness check
	 * 
	 * @param model
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public DigioIniResponseModel digioInitialize(DigioRequestModel model) throws ClientWebApplicationException {
		DigioIniResponseModel apiModel = null;
		try {
			apiModel = digioRestService.digioInitialize(props.getDigioAuthKey(), model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

	/**
	 * Method to check liveness check
	 * 
	 * @param model
	 * @return
	 * @throws ClientWebApplicationException
	 */
	public DigioSaveAddResponse saveDigiAddress(String reqId) throws ClientWebApplicationException {
		DigioSaveAddResponse apiModel = null;
		try {
			apiModel = digioRestService.saveDigiAddress(props.getDigioAuthKey(), reqId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

	public String getXml(String ReqId, long applicationId) {
		try {
			String message = digioRestService.getDigiXml(props.getDigioAuthKey(), ReqId, EkycConstants.AADHAR_KEYWORD,
					EkycConstants.AADHAR_XML);
			if (message != null) {
				String outputPath = props.getFileBasePath() + applicationId;
				Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
				if (isUserPresent.isPresent()) {
					String fileName = isUserPresent.get().getPanNumber() + EkycConstants.XMLNAME
							+ EkycConstants.XML_EXTENSION;
					String xmlPath = createXmlFile(outputPath, message, fileName);
					return xmlPath;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String createXmlFile(String basePath, String xmlContent, String fileName) {
		String filePath = null;
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		try {
			filePath = basePath + slash + fileName;
			File file = new File(filePath);
			file.getParentFile().mkdirs(); // Create parent directories if they don't exist

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(xmlContent.getBytes());
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	public String savePANXmlDocument(String requestId, long applicationId) {
	    try {
	        String fileSeparator = EkycConstants.UBUNTU_FILE_SEPERATOR;
	        Response response = digioRestService.getPanXml(props.getDigioAuthKey(), requestId, EkycConstants.ERP_PAN, EkycConstants.PAN_XML);

	        if (response.getStatus() == 200) {
	            byte[] pdfData = response.readEntity(byte[].class);
	            
	            if (pdfData != null && pdfData.length > 0) {
	            	Optional<ApplicationUserEntity> isUserPresent = repository.findById(applicationId);
	            	if (isUserPresent.isPresent()) {
						ApplicationUserEntity updatedUserDetails = isUserPresent.get();
						updatedUserDetails.setAadharPanLink("Y");
						updatedUserDetails = repository.save(updatedUserDetails);
	            	}
	                String fileName = savePdfContentToFile(pdfData, applicationId, EkycConstants.PAN_XML_NAME);
	                saveDocumentDetails(applicationId, fileName, props.getFileBasePath() + fileSeparator + applicationId + fileSeparator + fileName);
	                return fileName;
	            } else {
	                logger.error("PDF Content is null or empty.");
	            }
	        } else {
	            logger.error("Error getting PDF content. Status Code: {}", response.getStatus());
	        }
	    } catch (Exception e) {
	        logger.error("Error processing PAN XML document.", e);
	    }
	    return null;
	}

	public String getPdfContentFromResponse(String requestId) {
	    Response response = digioRestService.getPanXml(props.getDigioAuthKey(), requestId, EkycConstants.ERP_PAN, EkycConstants.PAN_XML);
	    
	    if (response.getStatus() == 200) {
	        return response.readEntity(String.class);
	    } else {
	        logger.error("Error getting PDF content. Status Code: {}", response.getStatus());
	    }

	    return null;
	}

	public String savePdfContentToFile(byte[] pdfData, long applicationId, String docType) {
	    try {
	        String location = props.getFileBasePath();
	        String fileSeparator = EkycConstants.UBUNTU_FILE_SEPERATOR;

	        if (OS.contains(EkycConstants.OS_WINDOWS)) {
	            fileSeparator = EkycConstants.WINDOWS_FILE_SEPERATOR;
	        }

	        String extension = ".pdf";
	        String fileName = applicationId + docType + extension;
	        String path = location + applicationId + fileSeparator + fileName;

	        File file = new File(path);
	        File folderCheck = new File(location + applicationId);

	        if (!folderCheck.exists()) {
	            folderCheck.mkdirs();
	        }

	        try (FileOutputStream outputStream = new FileOutputStream(file)) {
	            outputStream.write(pdfData);
	            logger.info("File saved successfully. FileName: {}", fileName);
	            return fileName;
	        } catch (IOException e) {
	            logger.error("Error saving PDF content to file: {}", e.getMessage(), e);
	        }
	    } catch (IllegalArgumentException e) {
	        logger.error("Error processing PDF content.", e);
	    }

	    return null;
	}

	public void saveDocumentDetails(long applicationId, String fileName, String documentPath) {
	    try {
	        DocumentEntity oldEntity = documentRepository.findByApplicationIdAndDocumentType(applicationId, EkycConstants.DOC_PAN);

	        if (oldEntity == null) {
	            DocumentEntity documentEntity = new DocumentEntity();
	            documentEntity.setApplicationId(applicationId);
	            documentEntity.setAttachementUrl(documentPath);
	            documentEntity.setAttachement(fileName);
	            documentEntity.setDocumentType(EkycConstants.DOC_PAN);
	            documentEntity.setTypeOfProof(EkycConstants.DOC_PAN);
	            documentRepository.save(documentEntity);
	        } else {
	            oldEntity.setAttachementUrl(documentPath);
	            oldEntity.setAttachement(fileName);
	            documentRepository.save(oldEntity);
	        }
	    } catch (Exception e) {
	        logger.error("Error saving document details.", e);
	    }
	}
}

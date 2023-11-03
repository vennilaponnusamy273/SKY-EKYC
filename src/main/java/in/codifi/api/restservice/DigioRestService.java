package in.codifi.api.restservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.DigioIniResponseModel;
import in.codifi.api.model.DigioRequestModel;
import in.codifi.api.model.DigioSaveAddResponse;
import in.codifi.api.repository.ApplicationUserRepository;
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
}

package in.codifi.api.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class DocumentHelper {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Convert base 64 to image and save in location
	 * 
	 * @param base64
	 * @param location
	 * @param applicationId
	 * @return
	 */
	public String convertBase64ToImage(String base64, long applicationId) {
		String location = props.getFileBasePath();
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		String base64String = base64;
		String[] strings = base64String.split(",");
		String extension;
		switch (strings[0]) {// check image's extension
		case "data:image/jpeg;base64":
			extension = "jpeg";
			break;
		case "data:image/png;base64":
			extension = "png";
			break;
		default:// should write cases for more images types
			extension = "jpg";
			break;
		}
		// convert base64 string to binary data
		byte[] data = DatatypeConverter.parseBase64Binary(strings[0]);
		String path = location + slash + applicationId + "_ivrImage." + extension;
		File file = new File(path);
		File foldercheck = new File(location);
		if (!foldercheck.exists()) {
			foldercheck.mkdirs();
		}
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

}

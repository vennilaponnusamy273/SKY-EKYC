package in.codifi.api.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.nsdl.esign.preverifiedNo.controller.EsignApplication;

import in.codifi.api.config.ApplicationProperties;

@ApplicationScoped
public class Esign {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	ApplicationProperties props;

//	static String esignPfxUserId = "V0158101";
//	static String esignPfxPassword = "12345678";
//	static String esignAspId = "ASPSCIPLMUMTEST263";
//	static String esignE_sign_alias = "le-57d1d81c-ca54-416d-b661-497077afcdce";
//	static String esignPfxLocation = "D:\\Pradeep\\my docs\\Esign\\eSIgn_E_SANTHOSH_KUMAR_eMudra_12345678.pfx";
//	static String esignPfxLocation= "D:\\Vicky\\Esign_Doc\\eSIgn_E_SANTHOSH_KUMAR_eMudra_12345678.pfx";
//	static String e_sign_tick_image = "D:\\CodiFi\\Documents\\tick.jpg";

//	static String txnPath = "D:\\Pradeep\\my docs\\Esign";

	public static void main(String[] args) throws IOException {
	}

	public void runMethod() {
		String getXml = getXmlForEsign();
		long timeInmillsecods = System.currentTimeMillis();
		String folderName = String.valueOf(timeInmillsecods);
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		toGetTxnFromXMlpath(props.getFileBasePath() + slash + folderName, getXml);
	}

	private String getXmlForEsign() {
		String response = "";

		try {
			String ekycID = "";
			String pdfReadServerPath = props.getEsignSamplePDF();
			String aspId = props.getEsignAspId();
			String authMode = "1";
			String responseUrl = "https://ekyc.nidhihq.com/ekyc-rest/user/testEsign";
			String p12CertificatePath = props.getEsignLocation();
			String p12CertiPwd = props.getEsignPassword();
			String tickImagePath = props.getEsignTickImage();
			int serverTime = 15;
			String alias = props.getEsignAlias();
			int pageNumberToInsertSignatureStamp = 1;
			String nameToShowOnSignatureStamp = "Test";
			String locationToShowOnSignatureStamp = "Madurai";
			String reasonForSign = "";
			int xCo_ordinates = 110;
			int yCo_ordinates = 100;
			int signatureWidth = 150;
			int signatureHeight = 100;
			String pdfPassword = "";
			String txn = "";
//			String respSignatureType  = ""; 

			try {
				EsignApplication eSignApp = new EsignApplication();
				response = eSignApp.getEsignRequestXml(ekycID, pdfReadServerPath, aspId, authMode, responseUrl,
						p12CertificatePath, p12CertiPwd, tickImagePath, serverTime, alias,
						pageNumberToInsertSignatureStamp, nameToShowOnSignatureStamp, locationToShowOnSignatureStamp,
						reasonForSign, xCo_ordinates, yCo_ordinates, signatureWidth, signatureHeight, pdfPassword, txn);
				System.out.println(response);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	private static String toGetTxnFromXMlpath(String xmlPath, String getXml) {
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			File chekcFile = new File(xmlPath);
			File myObj = new File(xmlPath + slash + "FirstResponse.xml");
			if (!chekcFile.exists()) {
				chekcFile.mkdirs();
			}
			if (myObj.createNewFile()) {
				FileWriter myWriter = new FileWriter(xmlPath + slash + "FirstResponse.xml");
				myWriter.write(getXml);
				myWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getXml;
	}

}

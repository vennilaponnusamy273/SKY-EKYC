package in.codifi.api.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nsdl.esign.preverifiedNo.controller.EsignApplication;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.PdfDataCoordinatesEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.PdfDataCoordinatesrepository;
import in.codifi.api.service.spec.IEsignGeneratorService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class EsignGeneratorService implements IEsignGeneratorService {
	@Inject
	ApplicationProperties props;
	@Inject
	PdfDataCoordinatesrepository pdfDataCoordinatesrepository;
	@Inject
	CommonMethods commonMethods;
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static final Logger logger = LogManager.getLogger(DocumentService.class);

	/**
	 * Method to get xml for E sign
	 */
	@Override
	public ResponseModel xmlGenerator(@NotNull long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		String response = "";
		try {
			String ekycID = Long.toString(applicationId);
			String pdfReadServerPath = props.getEsignSamplePDF();
			String aspId = props.getEsignAspId();
			String authMode = "1";
			String responseUrl = "https://ekyc.nidhihq.com/ekyc-rest/user/testEsign";
			String p12CertificatePath = props.getEsignLocation();
			String p12CertiPwd = props.getEsignPassword();
			String tickImagePath = props.getEsignTickImage();
			int serverTime = 15;
			String alias = props.getEsignAlias();
			String pdfPassword = "";
			String txn = "";
			String reasonForSign = "";
			String nameToShowOnSignatureStamp = "Test";
			String locationToShowOnSignatureStamp = "Madurai";
			try {
				EsignApplication eSignApp = new EsignApplication();
				ArrayList<Integer> xCoordinatesList = new ArrayList<>();
				ArrayList<Integer> yCoordinatesList = new ArrayList<>();
				ArrayList<Integer> PageNo = new ArrayList<>();
				ArrayList<Integer> height = new ArrayList<>();
				ArrayList<Integer> width = new ArrayList<>();
				List<PdfDataCoordinatesEntity> coordinatesList = pdfDataCoordinatesrepository
						.findByColumnNamesAndActiveStatus("esign", 1);
				if (coordinatesList != null) {
					for (PdfDataCoordinatesEntity entity : coordinatesList) {
						int xCoordinate = Integer.parseInt(entity.getXCoordinate());
						int yCoordinate = Integer.parseInt(entity.getYCoordinate());
						int pageNumber = Integer.parseInt(entity.getPageNo());
						xCoordinatesList.add(xCoordinate);
						yCoordinatesList.add(yCoordinate);
						PageNo.add(pageNumber + 1);
						height.add(35); // Change this to the actual height value
						width.add(90); // Change this to the actual width value
					}
				}
				response = eSignApp.getEsignRequestXml(ekycID, pdfReadServerPath, aspId, authMode, responseUrl,
						p12CertificatePath, p12CertiPwd, tickImagePath, serverTime, alias, nameToShowOnSignatureStamp,
						locationToShowOnSignatureStamp, reasonForSign, pdfPassword, txn, PageNo, xCoordinatesList,
						yCoordinatesList, height, width);
				
				System.out.println(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationId, "EsignGeneratorService", "xmlGenerator", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In xmlGenerator for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		responseModel.setResult(response);
		return responseModel;
	}

	@Override
	public ResponseModel toGetTxnFromXMlpath(String xmlPath, String getXml) {
		ResponseModel responseModel = new ResponseModel();
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
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "EsignGeneratorService", "toGetTxnFromXMlpath", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In toGetTxnFromXMlpath for the Error: "
							+ e.getMessage(),
					"ERR-001");
			e.printStackTrace();
		}
		responseModel.setResult(getXml);
		return responseModel;
	}

}

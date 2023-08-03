package in.codifi.api.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nsdl.esign.preverifiedNo.controller.EsignApplication;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.PdfDataCoordinatesEntity;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.entity.TxnDetailsEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.repository.PdfDataCoordinatesrepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.repository.TxnDetailsRepository;

@ApplicationScoped
@Service
public class Esign {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	ApplicationProperties props;
	@Inject
	NomineeRepository nomineeRepository;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	PdfDataCoordinatesrepository pdfDataCoordinatesrepository;
	@Inject
	TxnDetailsRepository txnDetailsRepository;
	@Inject
	CommonMethods commonMethods;

	public static void main(String[] args) throws IOException {
	}

	public ResponseModel runMethod(String OutPutPath, @NotNull long applicationId) {

		ResponseModel responseModel = new ResponseModel();
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		Optional<ApplicationUserEntity> applicationData = applicationUserRepository.findById(applicationId);
		String getXml = getXmlForEsignSinglePage(
				OutPutPath + applicationId + slash + applicationData.get().getPanNumber() + EkycConstants.PDF_EXTENSION,
				applicationId);
		long timeInmillsecods = System.currentTimeMillis();
		String folderName = String.valueOf(timeInmillsecods);
		if (getXml != null) {
			String filePath = props.getFileBasePath() + applicationId + slash + folderName;
			toCreateNewXMLFile(filePath, getXml);
			String txnId = toGetTxnFromXMlpath(filePath + slash + "FirstResponse.xml");
			if (StringUtil.isNotNullOrEmpty(txnId)) {
				TxnDetailsEntity savingEntity = new TxnDetailsEntity();
				savingEntity.setApplicationId(applicationId);
				savingEntity.setTxnId(txnId);
				savingEntity.setFolderLocation(filePath);
				TxnDetailsEntity savedEntity = txnDetailsRepository.save(savingEntity);
				if (savedEntity != null) {
					StringBuilder buff = new StringBuilder();
					buff.append(getXml);
					responseModel.setResult(buff);
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_CREATING_XML);
				}
			}
		}
		return responseModel;
	}

	private String getXmlForEsignSinglePage(String outPutPath, long applicationId) {
		String response = "";
		try {
			// Set up eSign application parameters
			String ekycID = "";
			String pdfReadServerPath = outPutPath;
			String aspId = props.getEsignAspId();
			String authMode = "1";
			String responseUrl = props.getESignReturnUrl();
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

			// Get PDF data coordinates from database
			List<PdfDataCoordinatesEntity> coordinatesList = pdfDataCoordinatesrepository
					.findByColumnNamesAndActiveStatus("esign", 1);
			if (coordinatesList != null) {
				// Set up lists for coordinates, page numbers, height and width
				ArrayList<Integer> xCoordinatesList = new ArrayList<>();
				ArrayList<Integer> yCoordinatesList = new ArrayList<>();
				ArrayList<Integer> PageNo = new ArrayList<>();
				ArrayList<Integer> height = new ArrayList<>();
				ArrayList<Integer> width = new ArrayList<>();
				Long countNominee = nomineeRepository.countByApplicationId(applicationId);
				SegmentEntity segmentEntity = segmentRepository.findByapplicationId(applicationId);
				if (countNominee == 0) {
					xCoordinatesList.add(430);
					yCoordinatesList.add(392);
					PageNo.add(17);
					height.add(40);
					width.add(100);
				} else {
					xCoordinatesList.add(80);
					yCoordinatesList.add(520);
					PageNo.add(16);
					height.add(40);
					width.add(100);
				}
				// Segment Esign
				int pageNoSegment = 12; // Change this to the desired page number
				int heightValue = 40; // Change this to the actual height value
				int widthValue = 100; // Change this to the actual width value
				if (segmentEntity.getComm() > 0) {
					xCoordinatesList.add(40);
					yCoordinatesList.add(100);
					PageNo.add(pageNoSegment);
					height.add(heightValue);
					width.add(widthValue);
				}
				if (segmentEntity.getConsent() > 0) {
					xCoordinatesList.add(130);
					yCoordinatesList.add(100);
					PageNo.add(pageNoSegment);
					height.add(heightValue);
					width.add(widthValue);
				}
				if (segmentEntity.getCd() > 0) {
					xCoordinatesList.add(220);
					yCoordinatesList.add(100);
					PageNo.add(pageNoSegment);
					height.add(heightValue);
					width.add(widthValue);
				}

				if (segmentEntity.getEd() > 0) {
					xCoordinatesList.add(300);
					yCoordinatesList.add(100);
					PageNo.add(pageNoSegment);
					height.add(heightValue);
					width.add(widthValue);
				}
				if (segmentEntity.getEquCash() > 0) {
					xCoordinatesList.add(370);
					yCoordinatesList.add(100);
					PageNo.add(pageNoSegment);
					height.add(heightValue);
					width.add(widthValue);
				}
				if (segmentEntity.getComm() > 0 || segmentEntity.getConsent() > 0 || segmentEntity.getCd() > 0
						|| segmentEntity.getEd() > 0 || segmentEntity.getEquCash() > 0) {
					xCoordinatesList.add(450);
					yCoordinatesList.add(100);
					PageNo.add(pageNoSegment);
					height.add(heightValue);
					width.add(widthValue);
				}
				// Loop through coordinates and add to respective lists
				for (PdfDataCoordinatesEntity entity : coordinatesList) {
					int xCoordinate = Integer.parseInt(entity.getXCoordinate());
					int yCoordinate = Integer.parseInt(entity.getYCoordinate());
					int pageNumber = Integer.parseInt(entity.getPageNo());
					xCoordinatesList.add(xCoordinate);
					yCoordinatesList.add(yCoordinate);
					PageNo.add(pageNumber + 1);
					height.add(40); // Change this to the actual height value
					width.add(100); // Change this to the actual width value
				}

				// Apply default coordinates to pages after the 38th page
				PDDocument document = PDDocument.load(new File(outPutPath));
				int pageCount = document.getNumberOfPages();
				if (pageCount > 38) {
					for (int i = 38; i < pageCount; i++) {
						xCoordinatesList.add(60);
						yCoordinatesList.add(300);
						PageNo.add(i + 1);
						height.add(40); // Change this to the actual height value
						width.add(100); // Change this to the actual width value
					}
				}

				// Generate eSign request XML using coordinates and other parameters
				EsignApplication eSignApp = new EsignApplication();
				response = eSignApp.getEsignRequestXml(ekycID, pdfReadServerPath, aspId, authMode, responseUrl,
						p12CertificatePath, p12CertiPwd, tickImagePath, serverTime, alias, nameToShowOnSignatureStamp,
						locationToShowOnSignatureStamp, reasonForSign, pdfPassword, txn, PageNo, xCoordinatesList,
						yCoordinatesList, height, width);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String getSignFromNsdl(String documentLocation, String documentToBeSavedLocation, String receivedXml,
			String applicantName, String city, long applicationID) {
		String responseText = null;
		try {
			String pathToPDF = documentLocation;
			String tickImagePath = props.getEsignTickImage();
			;
			int serverTime = 10;
			String nameToShowOnSignatureStamp = applicantName.toUpperCase();
			String locationToShowOnSignatureStamp = city.toUpperCase();
			String reasonForSign = "";
			String pdfPassword = "";
			String esignXml = receivedXml;
			String returnPath = documentToBeSavedLocation;
			try {
				EsignApplication eSignApp = new EsignApplication();
				List<PdfDataCoordinatesEntity> coordinatesList = pdfDataCoordinatesrepository
						.findByColumnNamesAndActiveStatus("esign", 1);
				if (coordinatesList != null) {
					ArrayList<Integer> xCoordinatesList = new ArrayList<>();
					ArrayList<Integer> yCoordinatesList = new ArrayList<>();
					ArrayList<Integer> PageNo = new ArrayList<>();
					ArrayList<Integer> height = new ArrayList<>();
					ArrayList<Integer> width = new ArrayList<>();
					Long countNominee = nomineeRepository.countByApplicationId(applicationID);
					if (countNominee == 0) {
						xCoordinatesList.add(430);
						yCoordinatesList.add(392);
						PageNo.add(17);
						height.add(40);
						width.add(100);
					} else {
						xCoordinatesList.add(80);
						yCoordinatesList.add(520);
						PageNo.add(16);
						height.add(40);
						width.add(100);
					}
					SegmentEntity segmentEntity = segmentRepository.findByapplicationId(applicationID);
					// Segment Esign
					int pageNoSegment = 12; // Change this to the desired page number
					int heightValue = 40; // Change this to the actual height value
					int widthValue = 100; // Change this to the actual width value
					if (segmentEntity.getComm() > 0) {
						xCoordinatesList.add(40);
						yCoordinatesList.add(100);
						PageNo.add(pageNoSegment);
						height.add(heightValue);
						width.add(widthValue);
					}
					if (segmentEntity.getConsent() > 0) {
						xCoordinatesList.add(130);
						yCoordinatesList.add(100);
						PageNo.add(pageNoSegment);
						height.add(heightValue);
						width.add(widthValue);
					}
					if (segmentEntity.getCd() > 0) {
						xCoordinatesList.add(220);
						yCoordinatesList.add(100);
						PageNo.add(pageNoSegment);
						height.add(heightValue);
						width.add(widthValue);
					}

					if (segmentEntity.getEd() > 0) {
						xCoordinatesList.add(300);
						yCoordinatesList.add(100);
						PageNo.add(pageNoSegment);
						height.add(heightValue);
						width.add(widthValue);
					}
					if (segmentEntity.getEquCash() > 0) {
						xCoordinatesList.add(370);
						yCoordinatesList.add(100);
						PageNo.add(pageNoSegment);
						height.add(heightValue);
						width.add(widthValue);
					}
					if (segmentEntity.getComm() > 0 || segmentEntity.getConsent() > 0 || segmentEntity.getCd() > 0
							|| segmentEntity.getEd() > 0 || segmentEntity.getEquCash() > 0) {
						xCoordinatesList.add(450);
						yCoordinatesList.add(100);
						PageNo.add(pageNoSegment);
						height.add(heightValue);
						width.add(widthValue);
					}
					// Apply default coordinates to pages after the 38th page
					PDDocument document = PDDocument.load(new File(documentLocation));
					int pageCount = document.getNumberOfPages();
					if (pageCount > 38) {
						for (int i = 38; i < pageCount; i++) {
							xCoordinatesList.add(60);
							yCoordinatesList.add(300);
							PageNo.add(i + 1);
							height.add(40); // Change this to the actual height value
							width.add(100); // Change this to the actual width value
						}
					}
					// Loop through coordinates and add to respective lists
					for (PdfDataCoordinatesEntity entity : coordinatesList) {
						int xCoordinate = Integer.parseInt(entity.getXCoordinate());
						int yCoordinate = Integer.parseInt(entity.getYCoordinate());
						int pageNumber = Integer.parseInt(entity.getPageNo());
						xCoordinatesList.add(xCoordinate);
						yCoordinatesList.add(yCoordinate);
						PageNo.add(pageNumber + 1);
						height.add(40); // Change this to the actual height value
						width.add(100); // Change this to the actual width value
					}

					responseText = eSignApp.getSignOnDocument(esignXml, pathToPDF, tickImagePath, serverTime,
							nameToShowOnSignatureStamp, locationToShowOnSignatureStamp, reasonForSign, pdfPassword,
							returnPath, PageNo, xCoordinatesList, yCoordinatesList, height, width);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseText;
	}

	private static String toCreateNewXMLFile(String xmlPath, String getXml) {
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

	public static String toGetTxnFromXMlpath(String xmlPath) {
		String txnId = "";
		try {
			File fXmlFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			Element eElement = doc.getDocumentElement();
			txnId = eElement.getAttribute("txn");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txnId;
	}

}

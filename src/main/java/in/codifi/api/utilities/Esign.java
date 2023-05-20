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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import com.nsdl.esign.preverifiedNo.controller.EsignApplication;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.PdfDataCoordinatesEntity;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.PdfDataCoordinatesrepository;
import in.codifi.api.repository.SegmentRepository;


@ApplicationScoped
@Service
public class Esign {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	ApplicationProperties props;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	PdfDataCoordinatesrepository pdfDataCoordinatesrepository;
	public static void main(String[] args) throws IOException {
	}
	public String runMethod( String OutPutPath, @NotNull long applicationId) {
		Optional<ApplicationUserEntity> applicationData = applicationUserRepository.findById(applicationId);
		//String getXml = getXmlForEsignSinglePage(OutPutPath,applicationId);
		String getXml = getXmlForEsignSinglePage(OutPutPath+applicationId+EkycConstants.WINDOWS_FILE_SEPERATOR+applicationData.get().getPanNumber()+".pdf",applicationId);
		long timeInmillsecods = System.currentTimeMillis();
		String folderName = String.valueOf(timeInmillsecods);
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		toGetTxnFromXMlpath(props.getFileBasePath() + slash + folderName, getXml);
		return getXml;
	}
	private String getXmlForEsignSinglePage(String outPutPath, long applicationId) {
	    String response = "";
	    try {
	        // Set up eSign application parameters
	        String ekycID = Long.toString(applicationId);
	        String pdfReadServerPath = outPutPath;
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
	        
	        // Get PDF data coordinates from database
	        List<PdfDataCoordinatesEntity> coordinatesList = pdfDataCoordinatesrepository.findByColumnNamesAndActiveStatus("esign", 1);
	        if (coordinatesList != null) {
	            // Set up lists for coordinates, page numbers, height and width
	            ArrayList<Integer> xCoordinatesList = new ArrayList<>();
	            ArrayList<Integer> yCoordinatesList = new ArrayList<>();
	            ArrayList<Integer> PageNo = new ArrayList<>();
	            ArrayList<Integer> height = new ArrayList<>();
	            ArrayList<Integer> width = new ArrayList<>();
	            SegmentEntity segmentEntity = segmentRepository.findByapplicationId(applicationId);
		         // Segment Esign
		    		int pageNoSegment = 12; // Change this to the desired page number
		    		int heightValue = 40; // Change this to the actual height value
		    		int widthValue = 100; // Change this to the actual width value
		    		if (segmentEntity.getComm() > 0) {
		    			xCoordinatesList.add(40);
		    		    yCoordinatesList.add(120);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		    xCoordinatesList.add(40);
		    		    yCoordinatesList.add(85);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		}
		    		if (segmentEntity.getConsent() > 0) {
		    			xCoordinatesList.add(160);
		    		    yCoordinatesList.add(120);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		    xCoordinatesList.add(160);
		    		    yCoordinatesList.add(85);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		} 
		    		if (segmentEntity.getEd() > 0) {
		    			xCoordinatesList.add(280);
		    		    yCoordinatesList.add(120);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		    xCoordinatesList.add(280);
		    		    yCoordinatesList.add(85);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		    xCoordinatesList.add(280);
		    		    yCoordinatesList.add(40);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		} 
		    		if (segmentEntity.getEquCash() > 0) {
		    			xCoordinatesList.add(390);
		    		    yCoordinatesList.add(120);
		    		    PageNo.add(pageNoSegment);
		    		    height.add(heightValue);
		    		    width.add(widthValue);
		    		    xCoordinatesList.add(390);
		    		    yCoordinatesList.add(85);
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
	            response = eSignApp.getEsignRequestXml(ekycID, pdfReadServerPath, aspId, authMode, responseUrl, p12CertificatePath,
	                    p12CertiPwd, tickImagePath, serverTime, alias, nameToShowOnSignatureStamp,
	                    locationToShowOnSignatureStamp, reasonForSign, pdfPassword, txn, PageNo, xCoordinatesList,
	                    yCoordinatesList, height, width);
	            System.out.println(response);
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

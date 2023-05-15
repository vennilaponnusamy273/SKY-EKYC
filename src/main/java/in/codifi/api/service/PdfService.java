package in.codifi.api.service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.GuardianEntity;
import in.codifi.api.entity.IvrEntity;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.entity.PdfDataCoordinatesEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.model.BankAddressModel;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.CkycResponseRepos;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.GuardianRepository;
import in.codifi.api.repository.IvrRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.repository.PdfDataCoordinatesrepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.restservice.RazorpayIfscRestService;
import in.codifi.api.service.spec.IPdfService;
import in.codifi.api.utilities.Esign;


@ApplicationScoped
public class PdfService implements IPdfService {

	
	@Inject
	AddressRepository addressRepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	GuardianRepository guardianRepository;
	@Inject
	IvrRepository ivrRepository;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	NomineeRepository nomineeRepository;
	@Inject
	DocumentRepository documentRepository;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	PdfDataCoordinatesrepository pdfDataCoordinatesrepository;
	@Inject
	ApplicationProperties props;
	@Inject
	CkycResponseRepos ckycResponseRepos;
	@Inject
	DocumentService documentService;
	@Inject
	Esign esign;
	@Inject
	DocumentRepository docrepository;
	
	@Inject
	RazorpayIfscRestService commonRestService;
	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	@Transactional
	public ResponseModel savePdf(PdfApplicationDataModel pdfModel) {
	    try {
	        Long applicationId = (long) pdfModel.getApplicationNo();
	        HashMap<String, String> map = mapping(applicationId);
	        File file = new File(props.getPdfsamplePdf()); 
	        PDDocument document = PDDocument.load(file);
	        List<PdfDataCoordinatesEntity> pdfDatas = pdfDataCoordinatesrepository.getCoordinates();
	        pdfInsertCoordinates(document, pdfDatas, map);
	        addDocument(document,(long) pdfModel.getApplicationNo());
	        document.save(props.getOutputPdf());
	        esign.runMethod(props.getOutputPdf(),pdfModel.getApplicationNo());
	        document.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
public void addDocument(PDDocument document, long applicationNo) {
    try {
        // Add a new page to the document
        String attachmentUrl = null;
        List<DocumentEntity> documents = docrepository.findByApplicationId(applicationNo);
        for (DocumentEntity entity : documents) {
            attachmentUrl = entity.getAttachementUrl();
            if (attachmentUrl.endsWith(".pdf")) {
                try (PDDocument attachment = PDDocument.load(new File(attachmentUrl))) {
                    PDFRenderer renderer = new PDFRenderer(attachment);
                    for (int i = 0; i < attachment.getNumberOfPages(); i++) {
                        PDPage page = new PDPage();
                        document.addPage(page);
                        BufferedImage image = renderer.renderImage(i);
                        PDRectangle pageSize = page.getMediaBox();
                        float imageWidth = image.getWidth();
                        float imageHeight = image.getHeight();
                        float centerX = (pageSize.getWidth() - imageWidth) / 2f;
                        float centerY = (pageSize.getHeight() - imageHeight) / 2f;
                        PDImageXObject importedPage = LosslessFactory.createFromImage(document, image);
                        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                            contentStream.drawImage(importedPage, centerX, centerY, imageWidth, imageHeight);
                        }
                    }
                }
            } 
            else {
            	BufferedImage image = ImageIO.read(new File(attachmentUrl));
            	PDPage page = new PDPage();
                document.addPage(page);
            	PDRectangle pageSize = page.getMediaBox();

            	// Calculate the maximum width and height that the image can occupy on the page
            	float maxWidth = pageSize.getWidth() * 0.8f;
            	float maxHeight = pageSize.getHeight() * 0.8f;

            	// Calculate the aspect ratio of the image
            	float aspectRatio = (float) image.getWidth() / (float) image.getHeight();

            	// Calculate the width and height of the image based on its aspect ratio and maximum size
            	float imageWidth = Math.min(maxWidth, maxHeight * aspectRatio);
            	float imageHeight = Math.min(maxHeight, maxWidth / aspectRatio);

            	// Calculate the position of the image on the page
            	float centerX = (pageSize.getWidth() - imageWidth) / 2f;
            	float centerY = (pageSize.getHeight() - imageHeight) / 2f;

            	PDImageXObject importedPage = LosslessFactory.createFromImage(document, image);
            	try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            	    contentStream.drawImage(importedPage, centerX, centerY, imageWidth, imageHeight);
            	}
            }
        }
        document.save(props.getOutputPdf());
    } catch (Exception e) {
        e.printStackTrace();
    }
}






	@SuppressWarnings({ "deprecation" })
	public void pdfInsertCoordinates(PDDocument document, List<PdfDataCoordinatesEntity> pdfDatas,
			HashMap<String, String> map) {
		try {
			//File f = new File("C:\\Users\\kgowt\\Downloads\\ARIALUNI.TTF");
			File f = new File(props.getPdfFontFile());
			PDFont font1 = PDTrueTypeFont.loadTTF(document, f);
			for (int i = 0; i < pdfDatas.size(); i++) {
				Float a = new Float(pdfDatas.get(i).getXCoordinate());
				float x = a.floatValue();
				Float b = new Float(pdfDatas.get(i).getYCoordinate());
				float y = b.floatValue();
				int pageNo = Integer.parseInt(pdfDatas.get(i).getPageNo());
				PDPage page = document.getPage(pageNo);
				PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
				if (pdfDatas.get(i).getColumnNames().equalsIgnoreCase("CurrentAddressLine1")
						|| pdfDatas.get(i).getColumnNames().equalsIgnoreCase("CurrentAddressLine2")
						|| pdfDatas.get(i).getColumnNames().equalsIgnoreCase("CurrentAddressLine3")
						|| pdfDatas.get(i).getColumnNames().equalsIgnoreCase("PermenentAddress1")
						|| pdfDatas.get(i).getColumnNames().equalsIgnoreCase("PermenentAddress2")
						|| pdfDatas.get(i).getColumnNames().equalsIgnoreCase("PermenentAddress3")) { // Check if the mapping is for an address
				    contentStream.setFont(font1, 7); // Set the font size to 5
				} else {
				    contentStream.setFont(font1, 7);
				}
				contentStream.setCharacterSpacing(0.4f);
				contentStream.beginText();
				contentStream.setNonStrokingColor(0, 0, 0);
				contentStream.newLineAtOffset(x, y);
				 if (pdfDatas.get(i).getColumnType().equalsIgnoreCase("text") || pdfDatas.get(i).getColumnType().equalsIgnoreCase("line")) {
				        String inputText;
				        if (pdfDatas.get(i).getColumnNames().equals("notApplicableMessage")) { // add this condition to check for the message
				            inputText = map.get("notApplicableMessage");
				            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 60); // set font and size
				            contentStream.setTextMatrix(Math.cos(Math.PI/4), Math.sin(Math.PI/4), -Math.sin(Math.PI/4), Math.cos(Math.PI/4), 100,280); // set text matrix for diagonal rotation and negative Y-axis direction
				        } else {
				            inputText = map.get(pdfDatas.get(i).getColumnNames());
				        }
				        if (inputText != null) {
				            inputText = inputText.replaceAll("\n", " ");
				            contentStream.showText(inputText.toUpperCase());
				        }
				} else if (pdfDatas.get(i).getColumnType().equalsIgnoreCase("tick")
						|| pdfDatas.get(i).getColumnType().equalsIgnoreCase("check box")) {
					 String tick = "\u2713";
					String inputText = map.get(pdfDatas.get(i).getColumnNames());
					if (inputText != null) {
						PDType1Font font = PDType1Font.ZAPF_DINGBATS; // Choose a font (e.g. Helvetica)
						int fontSize = 12; // Choose a font size (e.g. 12 points)
						 contentStream.setFont(font, fontSize);
					        contentStream.drawString(tick);
					}
				} else if (pdfDatas.get(i).getColumnType().equalsIgnoreCase("image")) {
					contentStream.endText();
					contentStream.close();
					String image =map.get(pdfDatas.get(i).getColumnNames());
					File imageFile = new File(image);
					BufferedImage bimg = ImageIO.read(imageFile);
					PDPageContentStream contentStream1 = new PDPageContentStream(document, page, true, true);
					//PDImageXObject pdImage = LosslessFactory.createFromImage(document, bimg);
					PDImageXObject pdImage=JPEGFactory.createFromImage(document, bimg, 0.5f);
					if (pageNo == 2) {
						contentStream1.drawImage(pdImage, x, y, 71, 76);
					} else if (pageNo == 5) {
						contentStream1.drawImage(pdImage, x, y, 72, 76);
					} else if (pageNo == 9) {
						contentStream1.drawImage(pdImage, x, y, 72, 76);
					} else if (pageNo == 11) {
						contentStream1.drawImage(pdImage, x, y, 71, 71);
					}
					contentStream1.close();
				}
				contentStream.close();
			}
			System.out.println("Completed");
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			Date date = new Date();
			System.out.println(formatter.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






	/** Method to put datas in HashMap */
	
	public HashMap<String, String> mapping(Long applicationId) {
		HashMap<String, String> map = new HashMap<>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();

		map.put("Date", formatter.format(date));
		map.put("eSignDate", formatter.format(date));
		map.put("MandatoryTick", "MandatoryTick");
		map.put("ApplicationNo", applicationId.toString());
		//map.put("esign","The Esign here");
		// FOR COLUMN REFERENCE
		/**map.put("I wish to open Trading Account Online : Yes", "yes");
		map.put("Whether you wish to receive communicaton : Yes", "yes");
		map.put("Whether you wish to receive Risk Disclosure Document  : Yes","yes");
		map.put("Online Trading Facility : Wireless", "yes");
		map.put("Online Trading Facility : Diet", "yes");
		map.put("Online Trading Facility : WEB", "yes");
		map.put("In additon to Digital Contract : Yes", "yes");
		map.put("Call & Trade /Ofine Trade/Intraday Square Of of : Yes", "yes");
		map.put("UPI/ Payment Gateway facility is available : Yes", "yes");
		map.put("Clearing Member Charges  : Yes", "yes");
		map.put("SMS Charges - Rs.50/- per month : Yes", "yes");
		map.put("Debit my trading account : Yes", "yes");
		map.put("Quarter", "yes");
		map.put("SMS & Email", "yes");**/
		
		//28 page mandatory
		/**map.put("Individual", "yes");
		map.put("Individual Resident", "yes");
		map.put("I wish to Open Trading / Demat Account Online : Yes", "yes");
		map.put("Automatc Credit : Yes", "yes");
		map.put("instructons in my/our account  : No", "yes");
		map.put("As per SEBI Regulaton", "yes");
		map.put("email ID : Yes", "yes");
		map.put("Electronic", "yes");
		map.put(" email ID with the RTA : Yes", "yes");
		map.put("the Aadhaar data and documents with KRA : Yes", "yes");
		map.put(" Annual Report : Electronic", "yes");
		map.put("bank account as given above through ECS : Yes", "yes");
		//Page 2 Fill by Profile details**/
		
		ProfileEntity profileEntity = profileRepository.findByapplicationId(applicationId);
		if (profileEntity != null) {
			map.put("UserName",profileEntity.getApplicantName());
			
			if (profileEntity.getGender().equalsIgnoreCase("Male")) {
				map.put("GenderMale", profileEntity.getGender());
				map.put("GenderPrefix", "MR");
			} else if (profileEntity.getGender().equalsIgnoreCase("Female")) {
				map.put("GenderFemale", profileEntity.getGender());
				map.put("GenderPrefix", "Ms");
			} else if (profileEntity.getGender().equalsIgnoreCase("Transgender")) {
				map.put("GenderTransgender", profileEntity.getGender());
			}
			
			if (profileEntity.getMaritalStatus().equalsIgnoreCase("Single")) {
				map.put("MaritalStatusSingle", profileEntity.getMaritalStatus());
			} else if (profileEntity.getMaritalStatus().equalsIgnoreCase("Married")) {
				map.put("MaritalStatusMarried", profileEntity.getMaritalStatus());
			}
			
			
			map.put("AnnualIncome", profileEntity.getAnnualIncome());
			if (profileEntity.getAnnualIncome().equalsIgnoreCase("Below Rs.1 Lac")) {
				map.put("Below Rs.1 Lac",profileEntity.getAnnualIncome());
			}
			else if (profileEntity.getAnnualIncome().equalsIgnoreCase("Rs.1-5 Lac")) {
				map.put("Rs.1-5 Lac",profileEntity.getAnnualIncome());
			}
			else if (profileEntity.getAnnualIncome().equalsIgnoreCase("Rs.5-10 Lac")) {
				map.put("Rs.5-10 Lac",profileEntity.getAnnualIncome());
			}
			
			else if (profileEntity.getAnnualIncome().equalsIgnoreCase("Rs.10-25 Lacc")) {
				map.put("Rs.10-25 Lac",profileEntity.getAnnualIncome());
			}
			else if (profileEntity.getAnnualIncome().equalsIgnoreCase("> Rs.25 Lac")) {
				map.put("> Rs.25 Lac",profileEntity.getAnnualIncome());
			}
			
			map.put("ApplicantName", profileEntity.getApplicantName());
			map.put("FatherName", profileEntity.getFatherName());
			map.put("Father's/Spouse Name", profileEntity.getFatherName());
			
			map.put("LegalAction", profileEntity.getLegalAction());
			
			map.put("MotherName", profileEntity.getMotherName());
			map.put("NetWoth", profileEntity.getNetWorth());
			map.put("NetWorthDate", profileEntity.getNetWorthDate());
			map.put("Occupation", profileEntity.getOccupation());
			 if (profileEntity.getOccupation().equalsIgnoreCase("Private Sector")) {
				map.put("Occupaton Private Sector",profileEntity.getOccupation());
			}
			 else  if (profileEntity.getOccupation().equalsIgnoreCase("Public Sector")) {
					map.put("Occupaton Public Sector",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Govt. Service")) {
					map.put("Occupaton Govt. Service ",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Business")) {
					map.put("Occupaton Business",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Professional")) {
					map.put("Occupaton Professional",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Agriculturist")) {
					map.put("Occupaton Agriculturist",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Retred")) {
					map.put("Occupaton Retred",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("House Wife")) {
					map.put("Occupaton House Wife",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Student")) {
					map.put("Occupaton Student",profileEntity.getOccupation());
				}
			 else if (profileEntity.getOccupation().equalsIgnoreCase("Others")) {
					map.put("Occupaton Others",profileEntity.getOccupation());
				}
			if (profileEntity.getPoliticalExposure().contains("yes")) {
				map.put("Please Tick, as Applicable Politcally Exposed Person (PEP) /", profileEntity.getPoliticalExposure());
			} else {
				map.put("Not a Politically Exposed Person :No", profileEntity.getPoliticalExposure());
			}
			map.put("SettlementCycle", profileEntity.getSettlementCycle());
			map.put("Title", profileEntity.getTitle());
			map.put("TradingExperience", profileEntity.getTradingExperience());
			
			
			 if (profileEntity.getTradingExperience().contains("0 years"))
			{
				map.put("No prior Experience", profileEntity.getTradingExperience());
			}
			 else
			 {
				 map.put("years in Commodites", profileEntity.getTradingExperience());
			 }
		}

		AddressEntity address = addressRepository.findByapplicationId(applicationId);
		if (address.getIsKra() == 1) {
			// For page 7 current address
			if(address.getKraAddress1()!=null)
			map.put("CurrentAddressLine1", address.getKraAddress1());
			else
			{
				map.put("CurrentAddressLine1", address.getAddress1());
			}
			if(address.getKraAddress2()!=null) {
			map.put("CurrentAddressLine2", address.getKraAddress2());
			}
			else
			{
				map.put("CurrentAddressLine2", address.getAddress2());
			}
			map.put("CurrentAddressLine3", address.getKraAddress3());
			if(address.getKraCity()!=null) {
				map.put("CurrentCity", address.getKraCity());
			}
			else
			{
				map.put("CurrentCity", address.getStreet());
			}
			if (address.getDistrict()!=null)
			{
				map.put("CurrentDistrict", address.getDistrict());// TODO
			}
			else
			{
				map.put("CurrentDistrict", address.getDistrict());// TODO
			}
			map.put("Place", address.getDistrict());
			if (address.getPincode() != null) {
				map.put("CurrentPincode", address.getPincode().toString());
			}else {
				map.put("CurrentPincode",Integer.toString(address.getKraPerPin()));
			}
			map.put("CurrentState", address.getState());
			map.put("CurrentCountry","INDIA");
			//For Page 
			map.put("PermenentAddress1", address.getKraPerAddress1());
			map.put("PermenentAddress2", address.getKraPerAddress2());
			map.put("PermenentAddress3", address.getKraPerAddress3());
			map.put("PermenentCity", address.getKraPerCity());
			map.put("Aadhaar Number", address.getAatharNo());
			map.put("Sole / First Holder’s Name UID", address.getAatharNo());
			map.put("PermenentDistrict", address.getDistrict());// TODO
			if (address.getKraPerPin() > 0) {
				map.put("PermenentPincode", Integer.toString(address.getKraPerPin()));
			} else {
				map.put("PermenentPincode", null);
			}
			if(address.getKraPerState()!=null) {
				map.put("PermenentState", address.getKraPerState());
			}
			else
			{
				map.put("PermenentState", address.getKraState());
			}
			map.put("PermenentCountry","INDIA");
		} else if (address.getIsdigi() == 1) {
			map.put("PermenentAddress1", address.getKraAddress1());
			map.put("PermenentAddress2", address.getKraAddress2());
			map.put("PermenentAddress3", address.getKraAddress3());
			map.put("PermenentCity", address.getKraCity());
			map.put("PermenentDistrict", address.getDistrict());
			map.put("Place", address.getDistrict());
			if (address.getPincode() != null) {
				map.put("PermenentPincode", address.getPincode().toString());
			} else {
				map.put("PermenentPincode", null);
			}
			map.put("PermenentState", address.getState());
			map.put("PermenentCountry","INDIA");
		}

		if (address.getIsKra()==1)
		{
			map.put("OthersProof",Integer.toString(address.getIsKra()));
			map.put("Others(Please Specify)","PAN");
		}
		else if(address.getIsdigi()==1)
		{
			map.put("OthersProof",Integer.toString(address.getIsdigi()));
			map.put("Others(Please Specify)","AATHAR CARD");	
		}
		map.put("ISO 3166 Country code","IN");
		
		Optional<ApplicationUserEntity> applicationData = applicationUserRepository.findById(applicationId);
		map.put("Applicaton Form No.", applicationId.toString());
		map.put("DP Internal Reference No", applicationId.toString());
		map.put("UserNameP2", applicationData.get().getUserName());
		map.put("Signature", applicationData.get().getUserName());
		map.put("Client Name", applicationData.get().getUserName());
		map.put("PanNumberP2", applicationData.get().getPanNumber());
		map.put("emailIDP2", applicationData.get().getEmailId());
		map.put("MobileNumberP2", applicationData.get().getMobileNo().toString());
		map.put("DOBP2", applicationData.get().getDob());

		
		map.put("FirstName", applicationData.get().getFirstName());
		map.put("MiddleName", applicationData.get().getMiddleName());
		map.put("LastName", applicationData.get().getLastName());
		map.put("MobileNumber", applicationData.get().getMobileNo().toString());
		map.put("PanNumber", applicationData.get().getPanNumber());
		map.put("emailID", applicationData.get().getEmailId());
		map.put("DOB", applicationData.get().getDob());

		BankEntity bankDetails = bankRepository.findByapplicationId(applicationId);
		map.put("Bank A/C Number*", bankDetails.getAccountNo());
		map.put("Bank Branch Address", bankDetails.getAddress());
		map.put("BranchName", bankDetails.getBranchName());
		map.put("RTGS/NEFT/IFSC Code", bankDetails.getIfsc());
		map.put("MICR", bankDetails.getMicr());
		map.put("VerifyAccNumber", bankDetails.getVerifyAccNumber());
		map.put("BankPincode", bankDetails.getPincode());
		
		BankAddressModel model= commonRestService.getBankAddressByIfsc(bankDetails.getIfsc());
		map.put("Bank Name",model.getBank());
		map.put("City",model.getCity());
		map.put("State",model.getState());
		map.put("Country","INDIA");
		map.put("Branch Name",model.getBranch());
		IvrEntity ivrEntity = ivrRepository.findByApplicationId(applicationId);
		map.put("latitude", ivrEntity.getLatitude());
		map.put("longitude", ivrEntity.getLongitude());
		map.put("ipvDoneOn", ivrEntity.getUpdatedOn().toString());
		map.put("Photo", ivrEntity.getAttachementUrl());
		map.put("image", ivrEntity.getAttachementUrl());
		if (ivrEntity.getAttachementUrl()!=null)
		{
			map.put("IPV DONE","yes");
			map.put("eSignDateOn",formatter.format(date));
		}
		ResponseCkyc responseCkyc=ckycResponseRepos.findByApplicationId(applicationId);
		map.put("Father's / Spouse Name - Prefix", responseCkyc.getFatherPrefix());
		if(responseCkyc.getFatherFname()!=null) {
		map.put("i)FFirst Name", responseCkyc.getFatherFname());
		map.put("ii)FMiddle Name", responseCkyc.getFatherMname());
		map.put("iii)FLast Name", responseCkyc.getFatherLname());
		}
		else
		{
			map.put("i)FFirst Name", profileEntity.getFatherName());	
		}
		map.put("Mother Name - Prefix", responseCkyc.getMotherPrefix());
		if(responseCkyc.getMotherFname()!=null) {
		map.put("i)MFirst Name", responseCkyc.getMotherFname());
		map.put("ii)MMiddle Name", responseCkyc.getMotherMname());
		map.put("iii)MLast Name", responseCkyc.getMotherLname());
		}
		else
		{
			map.put("i)MFirst Name",profileEntity.getMotherName());
		}
		List<NomineeEntity> nomineeEntity = nomineeRepository.findByapplicationId(applicationId);
		if (nomineeEntity.isEmpty())
		{
			
			// map.put("notApplicableMessage", "--Not Applicable--");
		}
		else {
			 map.put("notApplicableMessage", "Not Applicable");
		for (int i = 0; i < nomineeEntity.size(); i++) {
			System.out.println("NomOneAllocation ----- " + nomineeEntity.get(i).getAllocation());
			if (i == 0) {
				
				map.put("I/We wish to make a nominaton.", nomineeEntity.get(i).getFirstname());
				map.put("Details of 1st Nominee Name of the nominee(s)", nomineeEntity.get(i).getFirstname());
				if (nomineeEntity.get(i).getAllocation() > 0) {
					map.put("Details of 1st Nominee Share of each Nominee", Integer.toString(nomineeEntity.get(i).getAllocation()));
				} else {
					map.put("Details of 1st Nominee Share of each Nominee", null);
				}
				map.put("Details of 1st Nominee Relatonship with the Applicant (if any)", nomineeEntity.get(i).getRelationship());
				map.put("Details of 1st Nominee Address of Nominee(s)", nomineeEntity.get(i).getAddress1());
				map.put("Details of 1st Nominee City / Place:", nomineeEntity.get(i).getAddress2());
				map.put("Details of 1st Nominee State & Country:", nomineeEntity.get(i).getState());
				if (nomineeEntity.get(i).getPincode() != null) {
					map.put("Details of 1st Nominee PIN Code", nomineeEntity.get(i).getPincode().toString());
				} else {
					map.put("Details of 1st Nominee PIN Code", null);
				}
				
				if (nomineeEntity.get(i).getMobilenumber() > 0) {
					map.put("1stNMobilenumber", Long.toString(nomineeEntity.get(i).getMobilenumber()));
				} else {
					map.put("1stNMobilenumber", null);
				}
				map.put("1stNEmailaddress", nomineeEntity.get(i).getEmailaddress());
				map.put("1stNPancard", nomineeEntity.get(i).getPancard());
				map.put("Signature1stNFirstname", nomineeEntity.get(i).getFirstname());
				map.put("Name(s) of Holder(s) Sole/First Holder (Mr./Ms.)", nomineeEntity.get(i).getFirstname());
				String nomineeIdString = nomineeEntity.get(i).getNomineeId().toString().replaceAll("[^0-9]", "");
				long nomineeId = Long.parseLong(nomineeIdString);
				GuardianEntity guardianEntity = guardianRepository.findByNomineeId(nomineeId);
				if (guardianEntity != null) {
					map.put("1stNDateOfbirth", nomineeEntity.get(i).getDateOfbirth());
					map.put("Details of 1st Nominee Name of Guardian", guardianEntity.getFirstname());
					map.put("Details of 1st Nominee Address of Guardian(s)", guardianEntity.getAddress1());
					map.put("Details of 1st GNominee City / Place:", guardianEntity.getAddress2());
					map.put("Details of 1st GNominee State & Country:", guardianEntity.getState());
					if (guardianEntity.getPincode() != null) {
						map.put("Details of 1st GNominee PIN Code", guardianEntity.getPincode().toString());
					} else {
						map.put("Details of 1st GNominee PIN Code", null);
					}
					if (guardianEntity.getMobilenumber() > 0) {
						map.put("1stNGMobilenumber", Long.toString(guardianEntity.getMobilenumber()));
					} else {
						map.put("1stNGMobilenumber", null);
					}
					map.put("1stNGEmailaddress", guardianEntity.getEmailaddress());
					map.put("Details of 1st Nominee Relatonship of Guardian with nominee", guardianEntity.getRelationship());
					if(guardianEntity.getPancard()!=null) {
					map.put("1stNGPancardcheck", guardianEntity.getPancard());
					map.put("1stNGPancard", guardianEntity.getPancard());
					}
				}
			} else if (i == 1) {
				map.put("Details of  2nd Nominee Name of the nominee(s)", nomineeEntity.get(i).getFirstname());
				if (nomineeEntity.get(i).getAllocation() > 0) {
					map.put("Details of 2nd Nominee Share of each Nominee", Integer.toString(nomineeEntity.get(i).getAllocation()));
				} else {
					map.put("Details of 2nd Nominee Share of each Nominee", null);
				}
				map.put("Details of 2nd Nominee Relatonship with the Applicant (if any)", nomineeEntity.get(i).getRelationship());
				map.put("Details of 2nd Nominee Address of Nominee(s)", nomineeEntity.get(i).getAddress1());
				map.put("Details of 2nd Nominee City / Place:", nomineeEntity.get(i).getAddress2());
				map.put("Details of 2nd Nominee State & Country:", nomineeEntity.get(i).getState());
				if (nomineeEntity.get(i).getPincode() != null) {
					map.put("Details of 2ndNominee PIN Code", nomineeEntity.get(i).getPincode().toString());
				} else {
					map.put("Details of 2ndNominee PIN Code", null);
				}
				if (nomineeEntity.get(i).getMobilenumber() > 0) {
					map.put("2ndNMobilenumber", Long.toString(nomineeEntity.get(i).getMobilenumber()));
				} else {
					map.put("2ndNMobilenumber", null);
				}
				map.put("2ndNEmailaddress", nomineeEntity.get(i).getEmailaddress());
				map.put("Signature2ndNFirstname", nomineeEntity.get(i).getFirstname());
				map.put("Name(s) of Holder(s) Second Holder (Mr./Ms.)", nomineeEntity.get(i).getFirstname());
				map.put("2ndNPancard", nomineeEntity.get(i).getPancard());
				
				String nomineeIdString = nomineeEntity.get(i).getNomineeId().toString().replaceAll("[^0-9]", "");
				long nomineeId = Long.parseLong(nomineeIdString);
				GuardianEntity guardianEntity = guardianRepository.findByNomineeId(nomineeId);
				if (guardianEntity != null) {
					map.put("2ndNDateOfbirth",nomineeEntity.get(i).getDateOfbirth());
					map.put("Details of 2nd Nominee Name of Guardian", guardianEntity.getFirstname());
					map.put("Details of 2nd Nominee Address of Guardian(s)", guardianEntity.getAddress1());
					map.put("Details of 2nd Nominee City / Place of Guardian(s):", guardianEntity.getAddress2());
					map.put("Details of 2nd Nominee State & Country of Guardian(s):", guardianEntity.getState());
					if (guardianEntity.getPincode() != null) {
						map.put("Details of 2nd Nominee PIN Code of Guardian(s)", guardianEntity.getPincode().toString());
					} else {
						map.put("Details of 2nd Nominee PIN Code of Guardian(s)", null);
					}
					
					map.put("2ndNGEmailaddress", guardianEntity.getEmailaddress());
					map.put("Details of 2nd Nominee Relatonship of Guardian with nominee", guardianEntity.getRelationship());
					map.put("2ndNGPancard", guardianEntity.getPancard());
					if (guardianEntity.getMobilenumber() > 0) {
						map.put("2ndNGMobilenumber", Long.toString(guardianEntity.getMobilenumber()));
					} else {
						map.put("2ndNGMobilenumber", null);
					}
					if(guardianEntity.getPancard()!=null) {
						map.put("1stNGPancardcheck", guardianEntity.getPancard());
						map.put("2ndNGPancard", guardianEntity.getPancard());
						}
				}
			} else if (i == 2) {
				map.put("Details of 3rd Nominee Name of the nominee(s)", nomineeEntity.get(i).getFirstname());
				if (nomineeEntity.get(i).getAllocation() > 0) {
					map.put("Details of 3rd Nominee Share of each Nominee", Integer.toString(nomineeEntity.get(i).getAllocation()));
				} else {
					map.put("Details of 3rd Nominee Share of each Nominee", null);
				}
				map.put("Details of 3rd Nominee Relatonship with the Applicant (if any)", nomineeEntity.get(i).getRelationship());
				map.put("Details of 3rd Nominee Address of Nominee(s)", nomineeEntity.get(i).getAddress1());
				map.put("Details of 3rd Nominee City / Place:", nomineeEntity.get(i).getAddress2());
				map.put("Details of 3rd Nominee State & Country:", nomineeEntity.get(i).getState());
				if (nomineeEntity.get(i).getPincode() != null) {
					map.put("Details of 3rd Nominee PIN Code", nomineeEntity.get(i).getPincode().toString());
				} else {
					map.put("Details of 3rd Nominee PIN Code", null);
				}
				if (nomineeEntity.get(i).getMobilenumber() > 0) {
					map.put("3rdNMobilenumber", Long.toString(nomineeEntity.get(i).getMobilenumber()));
				} else {
					map.put("3rdNMobilenumber", null);
				}
				map.put("3rdNEmailaddress", nomineeEntity.get(i).getEmailaddress());
				map.put("Signature3rdNFirstname", nomineeEntity.get(i).getFirstname());
				map.put("Name(s) of Holder(s) Third Holder (Mr./Ms.)", nomineeEntity.get(i).getFirstname());
				map.put("3rdNPancard", nomineeEntity.get(i).getPancard());
				String nomineeIdString = nomineeEntity.get(i).getNomineeId().toString().replaceAll("[^0-9]", "");
				long nomineeId = Long.parseLong(nomineeIdString);
				GuardianEntity guardianEntity = guardianRepository.findByNomineeId(nomineeId);
				if (guardianEntity != null) {
					map.put("3rdNDateOfbirth",nomineeEntity.get(i).getDateOfbirth());
					map.put("Details of 3rd Nominee Name of Guardian", guardianEntity.getFirstname());
					map.put("Details of 3rd Nominee Address of Guardian(s)", guardianEntity.getAddress1());
					map.put("Details of 3rd GNominee City / Place:", guardianEntity.getAddress2());
					map.put("Details of 3rd GNominee State & Country:", guardianEntity.getState());
					if (guardianEntity.getPincode() != null) {
						map.put("Details of 3rd GNominee PIN Code", guardianEntity.getPincode().toString());
					} else {
						map.put("Details of 3rd GNominee PIN Code", null);
					}
					if (guardianEntity.getMobilenumber() > 0) {
						map.put("3rdNGMobilenumber", Long.toString(guardianEntity.getMobilenumber()));
					} else {
						map.put("3rdNGMobilenumber", null);
					}
					map.put("3rdNGEmailaddress", guardianEntity.getEmailaddress());
					
					map.put("3rdNGLastname", guardianEntity.getLastname());
					if (guardianEntity.getNomineeId() != null) {
						map.put("3rdNGNomineeId", guardianEntity.getNomineeId().toString());
					} else {
						map.put("3rdNGNomineeId", null);
					}
					if(guardianEntity.getPancard()!=null) {
						map.put("1stNGPancardcheck", guardianEntity.getPancard());
						map.put("3rdNGPancard", guardianEntity.getPancard());
						}
					map.put("Details of 3rd Nominee Relatonship of Guardian with nominee", guardianEntity.getRelationship());
					map.put("3rdNGRelationship", guardianEntity.getRelationship());
					
					
					
				}
			}

			}
		}
		


		SegmentEntity segmentEntity = segmentRepository.findByapplicationId(applicationId);
		if (segmentEntity.getCd() > 0) {
			map.put("BSECD", Integer.toString(segmentEntity.getCd()));
			map.put("NSECD", Integer.toString(segmentEntity.getCd()));
		} else {
			map.put("BSECD", null);
			map.put("NSECD", null);
		}
		if (segmentEntity.getComm() > 0) {
			map.put("BSE COMMODITY", Integer.toString(segmentEntity.getComm()));
			map.put("NSE COMMODITY", Integer.toString(segmentEntity.getComm()));
		} else {
			map.put("BSE COMMODITY", null);
			map.put("NSE COMMODITY", null);
		}
		if (segmentEntity.getConsent() > 0) {
			map.put("segmentConsent", Integer.toString(segmentEntity.getConsent()));
		} else {
			map.put("segmentConsent", null);
		}
		if (segmentEntity.getEd() > 0) {
			map.put("BSE F&O", Integer.toString(segmentEntity.getEd()));
			map.put("NSE F&O", Integer.toString(segmentEntity.getEd()));
		} else {
			map.put("BSE F&O", null);
			map.put("NSE F&O", null);
		}
		if (segmentEntity.getEquCash() > 0) {
			map.put("BSE Cash", Integer.toString(segmentEntity.getEquCash()));
			map.put("NSE Cash", Integer.toString(segmentEntity.getEquCash()));
		} else {
			map.put("BSE Cash", null);
			map.put("NSE Cash", null);
		}
		if (segmentEntity.getMutFunds() > 0) {
			map.put("segmentMutFunds", Integer.toString(segmentEntity.getMutFunds()));
		} else {
			map.put("segmentMutFunds", null);
		}
		map.put("segmentCategory", segmentEntity.getCategory());

		return map;
	}
	
}

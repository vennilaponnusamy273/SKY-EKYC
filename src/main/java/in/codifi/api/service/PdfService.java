package in.codifi.api.service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import in.codifi.api.entity.ReferralEntity;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.entity.TxnDetailsEntity;
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
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.repository.PdfDataCoordinatesrepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.repository.ReferralRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.repository.TxnDetailsRepository;
import in.codifi.api.restservice.RazorpayIfscRestService;
import in.codifi.api.service.spec.IPdfService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.Esign;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class PdfService implements IPdfService {

	private static String OS = System.getProperty("os.name").toLowerCase();
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
	KraKeyValueRepository kraKeyValueRepository;
	@Inject
	Esign esign;
	@Inject
	DocumentRepository docrepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	TxnDetailsRepository txnDetailsRepository;
	@Inject
	RazorpayIfscRestService commonRestService;
	@Inject
	ReferralRepository referralRepository;
	private static final Logger logger = LogManager.getLogger(PennyService.class);

	/**
	 * Method to save PDF
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Override
	@Transactional
	public Response savePdf(long applicationId) {
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		String outputPath = props.getFileBasePath() + applicationId;
		new File(outputPath).mkdir();
		try {
			Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(applicationId);
			if (userEntity.isPresent() && userEntity.get().getSmsVerified() == 1
					&& userEntity.get().getEmailVerified() == 1) {
				HashMap<String, String> map = mapping(applicationId);
				File file = new File(props.getPdfPath());
				PDDocument document = PDDocument.load(file);
				File fileAathar = new File(props.getAadharPdfPath());
				File filePan = new File(props.getPanPdfPath());
				File filename = null;
				if (map.get("aadharPDF") == "aadharPDF") {
					filename=fileAathar;
				} else if (map.get("panPDF") == "panPDF") {
					filename=filePan;
				} else {
					document = PDDocument.load(file);
				}
				PDDocument combine = PDDocument.load(filename);
				PDFMergerUtility merger = new PDFMergerUtility();
				merger.appendDocument(document, combine);
				merger.mergeDocuments();
				combine.close();
				if(fileAathar!=null||filePan!=null) {
				File verifyImageFile = new File(props.getVerifyImage());
				if (verifyImageFile.exists()) {
				    int pageIndex = 38; // Change this to the actual index of the page you want to add the image to
				    if (pageIndex >= 0 && pageIndex < document.getNumberOfPages()) {
				        PDPage page = document.getPage(pageIndex);
				        PDImageXObject importedVerifyImage = PDImageXObject.createFromFile(props.getVerifyImage(), document);
						
						// Create a new content stream for appending content to the existing page
						PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
						contentStream.drawImage(importedVerifyImage, 480, 60, 80, 80);
						contentStream.close(); // Close the content stream
				    } else {
				        System.err.println("Invalid page index.");
				    }
				} else {
				    System.err.println("Failed to load the verification image.");
				}}
				List<PdfDataCoordinatesEntity> pdfDatas = pdfDataCoordinatesrepository.getCoordinates();
				pdfInsertCoordinates(document, pdfDatas, map);
				addDocument(document, applicationId);
				addIPvDocument(document, applicationId);
				String fileName = userEntity.get().getPanNumber() + EkycConstants.PDF_EXTENSION;
				document.save(outputPath + slash + fileName);
				document.close();
				String path = outputPath + slash + fileName;
				try {
					applicationUserRepository.updateEsignStage(applicationId, EkycConstants.EKYC_STATUS_PDF_GENERATED,
							EkycConstants.PAGE_PDFDOWNLOAD, 0, 1, "");
				} catch (Exception e) {
					e.printStackTrace();
				}
				String contentType = URLConnection.guessContentTypeFromName(fileName);
				File savedFile = new File(path);
				ResponseBuilder response = Response.ok((Object) savedFile);
				response.type(contentType);
				response.header("Content-Disposition", "attachment;filename=" + savedFile.getName());
				return response.build();
			} else {
				if (userEntity.isEmpty()) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(MessageConstants.USER_ID_INVALID).build();
				} else {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(MessageConstants.USER_NOT_VERIFIED).build();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.FILE_NOT_FOUND).build();
	}
	public void addIPvDocument(PDDocument document, long applicationNo) {
		try {
			String attachmentUrl = null;
			IvrEntity ivrEntity = ivrRepository.findByApplicationId(applicationNo);
			if (ivrEntity != null) {
				attachmentUrl = ivrEntity.getAttachementUrl();
			}
			if(attachmentUrl!=null) {
			if (attachmentUrl.endsWith(".pdf")) {
				System.out.println(attachmentUrl);
				int originalPages = document.getNumberOfPages();
				try (PDDocument attachment = PDDocument.load(new File(attachmentUrl))) {
					File fileAadhar = new File(attachmentUrl);
					PDFMergerUtility merger = new PDFMergerUtility();
					PDDocument combine = PDDocument.load(fileAadhar);
					merger.appendDocument(document, combine);
					merger.mergeDocuments();
					combine.close();

					// The main document and attachment have been merged, and the verification image
					// can now be added
					
					int attachmentPages = attachment.getNumberOfPages();
					File verifyImageFile = new File(props.getVerifyImage());
					if (verifyImageFile.exists()) {
						 for (int i = originalPages; i < originalPages + attachmentPages; i++) {
				                PDPage page = document.getPage(i);
						//PDPage page = document.getPage(originalPages);
						try (PDPageContentStream contentStream = new PDPageContentStream(document, page, true,
								true)) {
							// Create the verification image as PDImageXObject
							PDImageXObject importedVerifyImage = PDImageXObject
									.createFromFile(props.getVerifyImage(), document);
							contentStream.drawImage(importedVerifyImage, 480, 420, 80, 80);
						}}
					}
				}
			} else {
				BufferedImage image = ImageIO.read(new File(attachmentUrl));
				PDPage page = new PDPage();
				document.addPage(page);
				PDRectangle pageSize = page.getMediaBox();

				// Calculate the maximum width and height that the image can occupy on the page
				float maxWidth = pageSize.getWidth() * 0.8f;
				float maxHeight = pageSize.getHeight() * 0.8f;

				// Calculate the aspect ratio of the image
				float aspectRatio = (float) image.getWidth() / (float) image.getHeight();

				// Calculate the width and height of the image based on its aspect ratio and
				// maximum size
				float imageWidth = Math.min(maxWidth, maxHeight * aspectRatio);
				float imageHeight = Math.min(maxHeight, maxWidth / aspectRatio);

				// Calculate the position of the image on the page
				float centerX = (pageSize.getWidth() - imageWidth) / 2f;
				float centerY = (pageSize.getHeight() - imageHeight) / 2f;

				// Load the verification image from image file (e.g., JPEG, PNG)
				File verifyImageFile = new File(props.getVerifyImage());
				if (verifyImageFile.exists()) {
					PDImageXObject importedPage = JPEGFactory.createFromImage(document, image, 0.5f);
					try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
						contentStream.drawImage(importedPage, centerX, centerY, imageWidth, imageHeight);
						// Create the verification image as PDImageXObject
						PDImageXObject importedVerifyImage = PDImageXObject
								.createFromFile(props.getVerifyImage(), document);
						contentStream.drawImage(importedVerifyImage, 480, 60, 80, 80);
					}
				} else {
					System.err.println("Failed to load the verification image.");
				}
			}
		}} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addDocument(PDDocument document, long applicationNo) {
		try {
			// Add a new page to the document
			String attachmentUrl = null;
			List<DocumentEntity> documents = docrepository.findByApplicationId(applicationNo);
			for (DocumentEntity entity : documents) {
				if (!StringUtil.isStrContainsWithEqIgnoreCase(entity.getAttachement(), "signedFinal.pdf")) {
					attachmentUrl = entity.getAttachementUrl();
					if (attachmentUrl.endsWith(".pdf")||attachmentUrl.endsWith(".PDF")) {
						//System.out.println(attachmentUrl);
						int originalPages = document.getNumberOfPages();
						try (PDDocument attachment = PDDocument.load(new File(attachmentUrl))) {
							File fileAadhar = new File(attachmentUrl);
							PDFMergerUtility merger = new PDFMergerUtility();
							PDDocument combine = PDDocument.load(fileAadhar);
							merger.appendDocument(document, combine);
							merger.mergeDocuments();
							combine.close();
							int attachmentPages = attachment.getNumberOfPages();
							File verifyImageFile = new File(props.getVerifyImage());
							if (verifyImageFile.exists()) {
								 for (int i = originalPages; i < originalPages + attachmentPages; i++) {
						                PDPage page = document.getPage(i);
								//PDPage page = document.getPage(originalPages);
								try (PDPageContentStream contentStream = new PDPageContentStream(document, page, true,
										true)) {
									// Create the verification image as PDImageXObject
									PDImageXObject importedVerifyImage = PDImageXObject
											.createFromFile(props.getVerifyImage(), document);
									contentStream.drawImage(importedVerifyImage, 480, 420, 80, 80);
								}}
							}
						}
					} else {
						BufferedImage image = ImageIO.read(new File(attachmentUrl));
						if (image != null) {
						PDPage page = new PDPage();
						document.addPage(page);
						PDRectangle pageSize = page.getMediaBox();

						// Calculate the maximum width and height that the image can occupy on the page
						float maxWidth = pageSize.getWidth() * 0.8f;
						float maxHeight = pageSize.getHeight() * 0.8f;

						// Calculate the aspect ratio of the image
						float aspectRatio = (float) image.getWidth() / (float) image.getHeight();

						// Calculate the width and height of the image based on its aspect ratio and
						// maximum size
						float imageWidth = Math.min(maxWidth, maxHeight * aspectRatio);
						float imageHeight = Math.min(maxHeight, maxWidth / aspectRatio);

						// Calculate the position of the image on the page
						float centerX = (pageSize.getWidth() - imageWidth) / 2f;
						float centerY = (pageSize.getHeight() - imageHeight) / 2f;

						// Load the verification image from image file (e.g., JPEG, PNG)
						File verifyImageFile = new File(props.getVerifyImage());
						if (verifyImageFile.exists()) {
							PDImageXObject importedPage = JPEGFactory.createFromImage(document, image, 0.5f);
							try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
								contentStream.drawImage(importedPage, centerX, centerY, imageWidth, imageHeight);
								// Create the verification image as PDImageXObject
								PDImageXObject importedVerifyImage = PDImageXObject
										.createFromFile(props.getVerifyImage(), document);
								contentStream.drawImage(importedVerifyImage, 480, 60, 80, 80);
							}
						} else {
							System.err.println("Failed to load the verification image.");
						}
					}
					}
				}
			}
			// document.save(props.getOutputPdf());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void pdfInsertCoordinates(PDDocument document, List<PdfDataCoordinatesEntity> pdfDatas,
			HashMap<String, String> map) {
		try {
			File fontFile = new File(props.getPdfFontFile());
			PDFont font = PDTrueTypeFont.loadTTF(document, fontFile);
			for (PdfDataCoordinatesEntity pdfData : pdfDatas) {
			//for (int i = 0; i < pdfDatas.size(); i++) {
				float x = Float.parseFloat(pdfData.getXCoordinate());
				float y = Float.parseFloat(pdfData.getYCoordinate());
				int pageNo = Integer.parseInt(pdfData.getPageNo());
				PDPage page = document.getPage(pageNo);
				PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
				contentStream.setFont(font, 7);
				PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
				graphicsState.setNonStrokingAlphaConstant(1f); // Set the alpha value to 1 (opaque)
				contentStream.setGraphicsStateParameters(graphicsState);
				contentStream.setCharacterSpacing(0.4f);
				String columnType = pdfData.getColumnType();
				String columnNames = pdfData.getColumnNames();
				if (columnType.equalsIgnoreCase("textDIGI") && map.get("aadharPDF") != null) {
					contentStream.beginText();
					contentStream.setNonStrokingColor(0, 0, 0);
					contentStream.newLineAtOffset(x, y);
					String inputText = map.get(columnNames);
					if (inputText != null) {
						inputText = inputText.replaceAll("\n", " ");
						contentStream.showText(inputText.toUpperCase());
					}
					contentStream.endText();
				} else if (columnType.equalsIgnoreCase("textKRA") && map.get("panPDF") != null) {
					contentStream.beginText();
					contentStream.setNonStrokingColor(0, 0, 0);
					contentStream.newLineAtOffset(x, y);
					String inputText = map.get(columnNames);
					if (inputText != null) {
						inputText = inputText.replaceAll("\n", " ");
						contentStream.showText(inputText.toUpperCase());
					}
					contentStream.endText();
				} else if (columnType.equalsIgnoreCase("text")
						|| columnType.equalsIgnoreCase("line")) {
					contentStream.beginText();
					contentStream.setNonStrokingColor(0, 0, 0);
					contentStream.newLineAtOffset(x, y);
					String inputText;
					if (pdfData.getColumnNames().equals("notApplicableMessage")
							|| pdfData.getColumnNames().equals("notApplicableMessageNominee")) {
						 inputText = map.get(columnNames);
						contentStream.setFont(PDType1Font.HELVETICA_BOLD, 60);
						contentStream.setTextMatrix(Math.cos(Math.PI / 4), Math.sin(Math.PI / 4),
								-Math.sin(Math.PI / 4), Math.cos(Math.PI / 4), 100, 280);
					} else {
						 inputText = map.get(columnNames);
					}
					if (inputText != null) {
						inputText = inputText.replaceAll("\n", " ");
						contentStream.showText(inputText.toUpperCase());
					}

					contentStream.endText();
				} else if (columnType.equalsIgnoreCase("tick") || columnType.equalsIgnoreCase("check box")) {
					String tick = "\u2713";
					String inputText = map.get(columnNames);
					if (inputText != null) {
						contentStream.beginText();
						contentStream.setFont(PDType1Font.ZAPF_DINGBATS, 12);
						contentStream.setNonStrokingColor(0, 0, 0);
						contentStream.newLineAtOffset(x, y);
						contentStream.showText(tick);
						contentStream.endText();
					}
				} else if (columnType.equalsIgnoreCase("image")) {
					String imageKey = columnNames;
					String image = map.get(imageKey);
					if (StringUtil.isNotNullOrEmpty(image)) {
						BufferedImage bimg = ImageIO.read(new File(image));
						// Adjust the width and height as needed
						float width = 72;
						float height = 70;
						PDImageXObject pdImage = JPEGFactory.createFromImage(document, bimg, 0.5f);
						contentStream.drawImage(pdImage, x, y, width, height);
					}
				}
				contentStream.close();
			}
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
		ProfileEntity profileEntity = profileRepository.findByapplicationId(applicationId);
		if (profileEntity != null) {
			map.put("UserName", profileEntity.getApplicantName());

			if (profileEntity.getGender().equalsIgnoreCase("Male")) {
				map.put("GenderMale", profileEntity.getGender());
				map.put("GenderPrefix", "MR");
			} else if (profileEntity.getGender().equalsIgnoreCase("Female")) {
				map.put("GenderFemale", profileEntity.getGender());
				map.put("GenderPrefix", "Ms");
			} else if (profileEntity.getGender().equalsIgnoreCase("Transgender")) {
				map.put("transgender", profileEntity.getGender());
			}
			if(profileEntity.getGender()!=null) {
			map.put("Gender*", profileEntity.getGender());
			}
			if (profileEntity.getMaritalStatus().equalsIgnoreCase("Single")) {
				map.put("MaritalStatusSingle", profileEntity.getMaritalStatus());
			} else if (profileEntity.getMaritalStatus().equalsIgnoreCase("Married")) {
				map.put("MaritalStatusMarried", profileEntity.getMaritalStatus());
			}
			if(profileEntity.getAnnualIncome()!=null) {
			map.put("AnnualIncome", profileEntity.getAnnualIncome());
			if (profileEntity.getAnnualIncome().equalsIgnoreCase("0-1 lakh")) {
				map.put("Below Rs.1 Lac", profileEntity.getAnnualIncome());
			} else if (profileEntity.getAnnualIncome().equalsIgnoreCase("1-5 lakhs")) {
				map.put("Rs.1-5 Lac", profileEntity.getAnnualIncome());
			} else if (profileEntity.getAnnualIncome().equalsIgnoreCase("5-10 lakhs")) {
				map.put("Rs.5-10 Lac", profileEntity.getAnnualIncome());
			}

			else if (profileEntity.getAnnualIncome().equalsIgnoreCase("10-20 lakhs")) {
				map.put("Rs.10-25 Lac", profileEntity.getAnnualIncome());
			} else if (profileEntity.getAnnualIncome().equalsIgnoreCase("More than 20 lakhs")) {
				map.put("> Rs.25 Lac", profileEntity.getAnnualIncome());
			}
			}
			map.put("ApplicantName", profileEntity.getApplicantName());
			map.put("FatherName", profileEntity.getFatherName());
			map.put("Father's/Spouse Name", profileEntity.getFatherName());

			map.put("LegalAction", profileEntity.getLegalAction());

			map.put("MotherName", profileEntity.getMotherName());
			map.put("NetWoth", profileEntity.getNetWorth());
			map.put("NetWorthDate", profileEntity.getNetWorthDate());
			if(profileEntity.getOccupation()!=null) {
			map.put("Occupation", profileEntity.getOccupation());
			if (profileEntity.getOccupation().equalsIgnoreCase("Private Sector")) {
				map.put("Occupaton Private Sector", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Public Sector")) {
				map.put("Occupaton Public Sector", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Govt. Service")) {
				map.put("Occupaton Govt. Service ", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Business")) {
				map.put("Occupaton Business", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Professional")) {
				map.put("Occupaton Professional", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Agriculturist")) {
				map.put("Occupaton Agriculturist", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Retred")) {
				map.put("Occupaton Retred", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("House Wife")) {
				map.put("Occupaton House Wife", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Student")) {
				map.put("Occupaton Student", profileEntity.getOccupation());
			} else if (profileEntity.getOccupation().equalsIgnoreCase("Others")) {
				map.put("Occupaton Others", profileEntity.getOccupation());
			}}
			if (profileEntity.getPoliticalExposure().equalsIgnoreCase("yes")) {
				map.put("Please Tick, as Applicable Politcally Exposed Person (PEP) /",
						profileEntity.getPoliticalExposure());
				map.put("Please Tick, as Applicable Related to a Politcally Exposed Person (PEP)",
						profileEntity.getPoliticalExposure());
			} else {
				map.put("Please Tick, as Applicable Not a Politcally Exposed Person (PEP)/",
						profileEntity.getPoliticalExposure());
				map.put("Please Tick, as Applicable Not a Related to a Politcally Exposed Person (PEP)",
						profileEntity.getPoliticalExposure());
			}
			map.put("SettlementCycle", profileEntity.getSettlementCycle());
			map.put("Title", profileEntity.getTitle());
			// map.put("TradingExperience", profileEntity.getTradingExperience());
			if (profileEntity.getTradingExperience().equalsIgnoreCase("No Experience")) {
				map.put("No prior Experience", profileEntity.getTradingExperience());
			} else {
				map.put("years in Commodites", profileEntity.getTradingExperience());
				map.put("years in CommoditesTick", profileEntity.getTradingExperience());
			}

		}

		AddressEntity address = addressRepository.findByapplicationId(applicationId);
		if (address != null) {
			if (address.getIsKra() == 1) {
				map.put("panPDF", "panPDF");
				String proofAddress = address.getKraaddressproof();
				if (proofAddress != null) {
					map.put("proof of address (POA)", proofAddress.substring(0, Math.min(70, proofAddress.length())));
					if (proofAddress.length() >= 80) {
						map.put("proof of address (POA)1",
								proofAddress.substring(70, Math.min(140, proofAddress.length())));
					}
				}
				map.put("proof of identity (POI)", address.getKraproofIdNumber());
				if (address.getKraPerAddress1() != null && address.getKraPerAddress2() != null
						&& address.getKraPerAddress3() != null) {
					map.put("dematAddress",
							address.getKraPerAddress1() + " " + address.getKraPerAddress2() + " "
									+ address.getKraPerAddress3() + " " + address.getKraPerCity() + " "
									+ Integer.toString(address.getKraPerPin()));
				} else if (address.getKraPerAddress1() != null && address.getKraPerAddress2() != null) {
					map.put("dematAddress", address.getKraPerAddress1() + " " + address.getKraPerAddress2() + " "
							+ address.getKraPerCity() + " " + Integer.toString(address.getKraPerPin()));
				} else if (address.getKraAddress1() != null) {
					map.put("dematAddress", address.getKraAddress1() + " " + address.getKraPerCity() + " "
							+ Integer.toString(address.getKraPerPin()));
				}
			 else {
				map.put("CurrentPincode", Integer.toString(address.getKraPerPin()));
				if (address.getKraPerAddress1() != null && address.getKraPerAddress2() != null
						&& address.getKraPerAddress3() != null) {
					map.put("dematAddress", address.getKraPerAddress1() + " " + address.getKraPerAddress2() + " "
							+ address.getKraPerAddress3() + " " + address.getKraPerCity());
				} else if (address.getKraPerAddress1() != null && address.getKraPerAddress2() != null) {
					map.put("dematAddress", address.getKraPerAddress1() + " " + address.getKraPerAddress2() + " "
							+ address.getKraPerCity());
				} else if (address.getKraAddress1() != null) {
					map.put("dematAddress", address.getKraAddress1() + " " + address.getKraPerCity());
				}
			}
			String dematAddress = map.get("dematAddress");
			if (dematAddress != null) {
				map.put("dematAddress1", dematAddress.substring(0, Math.min(70, dematAddress.length())));
				if (dematAddress.length() >= 80) {
					map.put("dematAddress2", dematAddress.substring(70, Math.min(140, dematAddress.length())));
				}
			}
				// For page 7 current address
				if (address.getKraAddress1() != null)
					map.put("CurrentAddressLine1", address.getKraPerAddress1());
				else {
					map.put("CurrentAddressLine1", address.getKraAddress1());
				}
				if (address.getKraAddress2() != null) {
					map.put("CurrentAddressLine2", address.getKraPerAddress2());
				} else {
					map.put("CurrentAddressLine2", address.getKraAddress2());
				}
				if (address.getIsdigi() == 1) {
					map.put("UID Aadhaar", "yes");
				} else if (address != null && address.getIsKra() == 1) {
					if (address.getKraaddressproof() != null) {
						if (address.getKraaddressproof().equalsIgnoreCase("PASSPORT")) {
							map.put("Passpost", address.getKraproofIdNumber());
							map.put("A-Passport Number", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("VOTER IDENTITY CARD")) {
							map.put("B-Voter ID Card", address.getKraproofIdNumber());
							map.put("Voter ID", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("DRIVING LICENSE")) {
							map.put("C-Driving License", address.getKraproofIdNumber());
							map.put("Driving Licence", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("RATION CARD")) {
							map.put("Ration Card", address.getKraaddressproof());
						} else if (address.getKraaddressproof()
								.equalsIgnoreCase("REGISTERED LEASE / SALE AGREEMENT OF RESIDENCE")) {
							map.put("Registered Lease/Sales Agreement of Residence", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("LATEST BANK ACCOUNT STATEMENT")) {
							map.put("Latest Bank A/C  Statement/Passbook", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("LATEST LAND LINE TELEPHONE BILL")) {
							map.put("Latest Telephone Bill(Only Land Line)", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("LATEST ELECTRICITY BILL")) {
							map.put("Latest Electricity Bill", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("GAS BILL")) {
							map.put("Latest Gas Bill", address.getKraaddressproof());
						} else if (address.getKraaddressproof().equalsIgnoreCase("AADHAAR")) {
							map.put("UID Aadhaar", address.getKraaddressproof());
							map.put("F-Proof of Possission of Aadhaar", address.getKraproofIdNumber());
							map.put("Others(Please Specify)", address.getKraaddressproof());
							map.put("OthersProof", Integer.toString(address.getIsKra()));
							map.put("Sole / First Holder’s Name UID", address.getKraproofIdNumber());
							map.put("Aadhaar Number", address.getKraproofIdNumber());
						} else {
							map.put("OthersProof*", address.getKraaddressproof());
							map.put("Others(Please Specify)", address.getKraaddressproof());
							map.put("OthersProof", Integer.toString(address.getIsKra()));
						}
					}
				}
				map.put("CurrentAddressLine3", address.getKraAddress3());
				if (address.getKraCity() != null) {
					map.put("CurrentCity", address.getKraPerCity());
				} else {
					map.put("CurrentCity", address.getKraCity());
				}
				if (address.getDistrict() != null) {
					map.put("CurrentDistrict", address.getKraPerCity());
				} else {
					map.put("CurrentDistrict", address.getKraCity());// TODO
				}
				if (address.getPincode() != null) {
					map.put("CurrentPincode", Integer.toString(address.getKraPerPin()));
				} else {
					map.put("CurrentPincode", Integer.toString(address.getKraPin()));
				}
				map.put("Place", address.getKraPerCity());
				map.put("CurrentState1", address.getKraState());
				map.put("CurrentCountry", "INDIA");
				// For Page
				map.put("PermenentAddress1", address.getKraPerAddress1());
				map.put("PermenentAddress2", address.getKraPerAddress2());
				map.put("PermenentAddress3", address.getKraPerAddress3());
				map.put("PermenentCity", address.getKraPerCity());
				//map.put("Aadhaar Number", address.getAadharNo());
				//map.put("Sole / First Holder’s Name UID", address.getAadharNo());
				map.put("PermenentDistrict", address.getKraPerCity());// TODO
				if (address.getKraPerPin() > 0) {
					map.put("PermenentPincode", Integer.toString(address.getKraPerPin()));
				} else {
					map.put("PermenentPincode", null);
				}
				if (address.getKraPerState() != null) {
					map.put("PermenentState", address.getKraPerState());
				} else {
					map.put("PermenentState", address.getKraState());
				}
				map.put("PermenentCountry", "INDIA");
			} else if (address.getIsdigi() == 1) {
				map.put("aadharPDF", "aadharPDF");	
				if (address != null) {
				    StringBuilder addressBuilder = new StringBuilder();

				    if (address.getFlatNo() != null) {
				        addressBuilder.append(address.getFlatNo());
				    }

				    if (address.getStreet() != null) {
				        if (addressBuilder.length() > 0) {
				            addressBuilder.append(" ");
				        }
				        addressBuilder.append(address.getStreet());
				    }

				    if (address.getLandmark() != null) {
				        if (addressBuilder.length() > 0) {
				            addressBuilder.append(" ");
				        }
				        addressBuilder.append(address.getLandmark());
				    }

				    if (address.getAddress1() != null) {
				        if (addressBuilder.length() > 0) {
				            addressBuilder.append(" ");
				        }
				        addressBuilder.append(address.getAddress1());
				    }
				    if (address.getAddress2() != null) {
				        if (addressBuilder.length() > 0) {
				            addressBuilder.append(" ");
				        }
				        addressBuilder.append(address.getAddress2());
				    }
				    String fullAddress = addressBuilder.toString();
				    System.out.println("the fullAddress" + fullAddress);
				    map.put("PermanentAddress", fullAddress);
				}

				String dematAddress = map.get("PermanentAddress");
				if (dematAddress != null) {
				    map.put("PermenentAddress1", dematAddress.substring(0, Math.min(80, dematAddress.length())));
				    if (dematAddress.length() >= 80) {
				        map.put("PermenentAddress2", dematAddress.substring(80, Math.min(200, dematAddress.length())));
				    }
				    map.put("CurrentAddressLine1", dematAddress.substring(0, Math.min(80, dematAddress.length())));
				    if (dematAddress.length() >= 80) {
				        map.put("CurrentAddressLine2", dematAddress.substring(80, Math.min(200, dematAddress.length())));
				    }
				    map.put("dematAddress1", dematAddress.substring(0, Math.min(80, dematAddress.length())));
				    if (dematAddress.length() >= 80) {
				        map.put("dematAddress2", dematAddress.substring(80, Math.min(200, dematAddress.length())));
				    }

				    map.put("PermenentAddress1ForDIGI", dematAddress.substring(0, Math.min(40, dematAddress.length())));
				    if (dematAddress.length() >= 40) {
				        map.put("PermenentAddress2ForDIGI", dematAddress.substring(40, Math.min(80, dematAddress.length())));
				    }
				    if (dematAddress.length() >= 80) {
				        map.put("PermenentAddress3ForDIGI", dematAddress.substring(80, Math.min(120, dematAddress.length())));
				    }
				}

				//map.put("PermenentAddress1", address.getAddress1());
				//map.put("CurrentAddressLine1", address.getAddress1());
				//map.put("PermenentAddress2", address.getAddress2());
			  //map.put("CurrentAddressLine2", address.getAddress2());
				//map.put("PermenentAddress3", "");
				
				if (address.getLandmark() != null && !address.getLandmark().isEmpty()) {
				    map.put("CurrentCity", address.getLandmark());
				    map.put("PermenentCity", address.getLandmark());
				} else {
				    map.put("CurrentCity", address.getAddress1());
				    map.put("PermenentCity", address.getAddress1());
				}

				map.put("PermenentDistrict", address.getDistrict());
				map.put("CurrentDistrict", address.getDistrict());
				if (address.getDistrict() != null) {
					map.put("Place", address.getDistrict());
				} else if (address.getLandmark() != null) {
					map.put("Place", address.getLandmark());
				}
				if (address.getPincode() != null) {
					map.put("PermenentPincode", address.getPincode().toString());
					map.put("CurrentPincode", address.getPincode().toString());
				} else {
					map.put("PermenentPincode", null);
					map.put("CurrentPincode", null);
				}
				if (address.getLandmark() != null||!address.getLandmark().isEmpty()) {
					map.put("landmark", address.getLandmark());
				}else {
					map.put("landmark", address.getStreet());
				}
				map.put("CurrentState1", address.getState());
				map.put("PermenentState", address.getState());
				map.put("PermenentCountry", "INDIA");
				// map.put("Place", address.getKraPerCity());
				map.put("CurrentCountry", "INDIA");
			}

			if (address.getIsdigi() == 1) {
				map.put("s/o,c/o", address.getCo());
				map.put("OthersProof", Integer.toString(address.getIsdigi()));
				map.put("Others(Please Specify)", "AADHAR CARD");
				map.put("UID Aadhaar", Integer.toString(address.getIsdigi()));
				map.put("Aadhaar Number", address.getAadharNo());
				map.put("F-Proof of Possission of Aadhaar", address.getAadharNo());
				map.put("Sole / First Holder’s Name UID", address.getAadharNo());
				if (address != null && address.getIsdigi() == 1) {
					map.put("aadharPDF", "aadharPDF");
					DocumentEntity documents = docrepository.findByApplicationIdAndDocumentType(applicationId, "AADHAR_IMAGE");
					if (documents != null && documents.getAttachementUrl() != null) {
						map.put("imagedigi", documents.getAttachementUrl());
					}
				}
				
			}}
			// Below use to get stateCode from kraTable
			/**
			 * String state = null; String stateCode = null;
			 * 
			 * if (address.getState() != null) { state = address.getState(); } else if
			 * (address.getKraPerState() != null) { state = address.getKraPerState(); }
			 * 
			 * if (state != null) { KraKeyValueEntity kraKeyValueEntity =
			 * kraKeyValueRepository .findByMasterNameAndMasterIdAndKraValue("STATE", "1",
			 * state); if (kraKeyValueEntity != null) { stateCode =
			 * kraKeyValueEntity.getKraKey(); } }
			 * 
			 * map.put("stateCode", stateCode);
			 **/
		
		map.put("ISO 3166 Country code", "IN");

		Optional<ApplicationUserEntity> applicationData = applicationUserRepository.findById(applicationId);
		if (applicationData != null) {
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
			if (StringUtil.isNotNullOrEmpty(applicationData.get().getUccCodePrefix())
					|| StringUtil.isNotNullOrEmpty(applicationData.get().getUccCodeSuffix())) {
				map.put("ClientCode",
						applicationData.get().getUccCodePrefix() + applicationData.get().getUccCodeSuffix());
			}
		}
		BankEntity bankDetails = bankRepository.findByapplicationId(applicationId);
		if (bankDetails != null) {
			map.put("Bank A/C Number*", bankDetails.getAccountNo());
			map.put("Bank Branch Address", bankDetails.getAddress());
			map.put("BranchName", bankDetails.getBranchName());
			map.put("RTGS/NEFT/IFSC Code", bankDetails.getIfsc());
			map.put("MICR", bankDetails.getMicr());
			map.put("VerifyAccNumber", bankDetails.getVerifyAccNumber());
			map.put("BankPincode", bankDetails.getPincode());
			BankAddressModel model = commonRestService.getBankAddressByIfsc(bankDetails.getIfsc());
			if (model != null) {
				map.put("Bank Name", model.getBank());
				map.put("City", model.getCity());
				map.put("State", model.getState());
				map.put("Country", "INDIA");
				map.put("Branch Name", model.getBranch());
			}
		}

		IvrEntity ivrEntity = ivrRepository.findByApplicationId(applicationId);
		if (ivrEntity != null) {
			map.put("latitude", ivrEntity.getLatitude());
			map.put("longitude", ivrEntity.getLongitude());
			map.put("ipvDoneOn", ivrEntity.getUpdatedOn().toString());
			map.put("Photo", ivrEntity.getAttachementUrl());
			map.put("image", ivrEntity.getAttachementUrl());
			if (ivrEntity.getAttachementUrl() != null) {
				map.put("IPV DONE", "yes");
				map.put("eSignDateOn", formatter.format(date));
			}
		}
		ReferralEntity referralEntity = referralRepository.findByMobileNo(applicationData.get().getMobileNo());
		if(referralEntity!=null) {
			if (referralEntity.getName() != null) {
				map.put("Name of the Introducer", referralEntity.getRefByName());
				map.put("Signature of the Introducer", referralEntity.getRefByName());
				map.put("Status of the Introducer Existng Client", referralEntity.getRefByName());
			}else if (referralEntity.getReferralBy() != null) {
				map.put("Name of the Introducer",referralEntity.getReferralBy());
				map.put("Signature of the Introducer",referralEntity.getReferralBy());
				map.put("Status of the Introducer Existng Client",referralEntity.getReferralBy());
			}
			if (referralEntity.getRefByBranch() != null) {
				map.put("Address of the Introducer", referralEntity.getRefByBranch());
			}}
		ResponseCkyc responseCkyc = ckycResponseRepos.findByApplicationId(applicationId);
			if(profileEntity.getFatherName() != null) {
			map.put("i)FFirst Name", profileEntity.getFatherName());
			map.put("Father's / Spouse Name - Prefix", "MR");
			map.put("ii)FMiddle Name","");
			map.put("iii)FLast Name","");
			}else {
				if (responseCkyc.getFatherPrefix() != null || !responseCkyc.getFatherPrefix().isEmpty()) {
					map.put("Father's / Spouse Name - Prefix", responseCkyc.getFatherPrefix());
				} else if (responseCkyc.getFatherFname() != null ) {
					map.put("Father's / Spouse Name - Prefix", "MR");
				}
				if (responseCkyc.getFatherFname() != null) {
					map.put("i)FFirst Name", responseCkyc.getFatherFname());
					map.put("ii)FMiddle Name", responseCkyc.getFatherMname());
					map.put("iii)FLast Name", responseCkyc.getFatherLname());
				} 
			}
			if(profileEntity.getMotherName() != null) {
				map.put("i)MFirst Name", profileEntity.getMotherName());
				map.put("Mother Name - Prefix", "MRS");
				map.put("ii)MMiddle Name","");
				map.put("iii)MLast Name","");
			}
			else {
				if (StringUtil.isNotNullOrEmpty(responseCkyc.getMotherPrefix())) {
					map.put("Mother Name - Prefix", responseCkyc.getMotherPrefix());
				//	System.out.println("the " + responseCkyc.getMotherPrefix());
				} else if (responseCkyc.getMotherFullname() != null) {
					map.put("Mother Name - Prefix", "MRS");
				}
				if (responseCkyc.getMotherFname() != null) {
					map.put("i)MFirst Name", responseCkyc.getMotherFname());
					map.put("ii)MMiddle Name", responseCkyc.getMotherMname());
					map.put("iii)MLast Name", responseCkyc.getMotherLname());
				}}
		SegmentEntity segmentEntity = segmentRepository.findByapplicationId(applicationId);
		if (segmentEntity != null) {
		    StringBuilder notTradeBuilder = new StringBuilder();
		    if (segmentEntity.getComm() == 0) {
		        notTradeBuilder.append("NSE COMMODITY,BSE COMMODITY,MCX COMMODITY");
		    }
		    if (segmentEntity.getConsent() == 0) {
		        if (notTradeBuilder.length() > 0) {
		            notTradeBuilder.append(",");
		        }
		        notTradeBuilder.append("NSE ED,BSE ED");
		    }
		    if (segmentEntity.getCd() == 0) {
		        if (notTradeBuilder.length() > 0) {
		            notTradeBuilder.append(",");
		        }
		        notTradeBuilder.append("NSE CD,BSE CD");
		    }
		    if (segmentEntity.getEd() == 0) {
		        if (notTradeBuilder.length() > 0) {
		            notTradeBuilder.append(",");
		        }
		        notTradeBuilder.append("NSE F&O,BSE F&O");
		    }
		    if (segmentEntity.getEquCash() == 0) {
		        if (notTradeBuilder.length() > 0) {
		            notTradeBuilder.append(",");
		        }
		        notTradeBuilder.append("NSE CM,BSE CM,MUTUAL FUND");
		    }
		    String TradeBuilder=notTradeBuilder.toString();
		    map.put("not wish to trade",TradeBuilder.substring(0, Math.min(28, TradeBuilder.length())));
		    if(TradeBuilder.length() >=28) {
		    	  map.put("not wish to trade1",TradeBuilder.substring(28, Math.min(180, TradeBuilder.length())));
		    }
		}

		List<NomineeEntity> nomineeEntity = nomineeRepository.findByapplicationId(applicationId);
		if (nomineeEntity == null || nomineeEntity.isEmpty()) {
			// nomineeEntity is null, set "notApplicableMessageNominee" to "Not Applicable"
			map.put("notApplicableMessageNominee", "Not Applicable");
			map.put("Client NameNomineeopt", applicationData.get().getUserName());
			if (StringUtil.isNotNullOrEmpty(applicationData.get().getUccCodePrefix())
					|| StringUtil.isNotNullOrEmpty(applicationData.get().getUccCodeSuffix())) {
				map.put("ClientCodeopt",
						applicationData.get().getUccCodePrefix() + applicationData.get().getUccCodeSuffix());
			}
		} else if (!nomineeEntity.isEmpty()) {
			if (StringUtil.isNotNullOrEmpty(applicationData.get().getUccCodePrefix())
					|| StringUtil.isNotNullOrEmpty(applicationData.get().getUccCodeSuffix())) {
				map.put("ClientCodenopt",
						applicationData.get().getUccCodePrefix() + applicationData.get().getUccCodeSuffix());
			}
			map.put("Client NameNominee", applicationData.get().getUserName());
			map.put("notApplicableMessage", "Not Applicable");
			for (int i = 0; i < nomineeEntity.size(); i++) {
				if (i == 0) {
					map.put("I/We wish to make a nominaton.", nomineeEntity.get(i).getFirstname());
					map.put("Details of 1st Nominee Name of the nominee(s)", nomineeEntity.get(i).getFirstname());
					if (nomineeEntity.get(i).getAllocation() > 0) {
						map.put("Details of 1st Nominee Share of each Nominee",
								Integer.toString(nomineeEntity.get(i).getAllocation()));
					} else {
						map.put("Details of 1st Nominee Share of each Nominee", null);
					}
					map.put("Details of 1st Nominee Relatonship with the Applicant (if any)",
							nomineeEntity.get(i).getRelationship());

					if (nomineeEntity.get(i).getAddress1() != null) {
						String addressOfNominee = nomineeEntity.get(i).getAddress1();
						map.put("Details of 1st Nominee Address of Nominee(s)",
								addressOfNominee.substring(0, Math.min(26, addressOfNominee.length())));
						if (addressOfNominee.length() >= 26) {
							map.put("Details of 1st Nominee Address1 of Nominee(s)",
									addressOfNominee.substring(26, Math.min(52, addressOfNominee.length())));
						}
						if (addressOfNominee.length() >= 52) {
							map.put("Details of 1st Nominee Address2 of Nominee(s)",
									addressOfNominee.substring(52, Math.min(78, addressOfNominee.length())));
						}

					}
					if (nomineeEntity.get(i).getAddress2() != null) {
						String addressOfNominee = nomineeEntity.get(i).getAddress2();
						map.put("Details of 1st Nominee City / Place:",
								addressOfNominee.substring(0, Math.min(26, addressOfNominee.length())));
					}
					// map.put("Details of 1st Nominee Address of Nominee(s)",
					// nomineeEntity.get(i).getAddress1());
					// map.put("Details of 1st Nominee City / Place:",
					// nomineeEntity.get(i).getAddress2());
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
					if (nomineeEntity.get(i).getEmailaddress() != null) {
						String emailOfNominee = nomineeEntity.get(i).getEmailaddress();
						map.put("1stNEmailaddress", emailOfNominee.substring(0, Math.min(26, emailOfNominee.length())));
					}
					// map.put("1stNEmailaddress", nomineeEntity.get(i).getEmailaddress());
					map.put("1stNPancard", nomineeEntity.get(i).getPancard());
					map.put("Signature1stNFirstname", nomineeEntity.get(i).getFirstname());
					map.put("Name(s) of Holder(s) Sole/First Holder (Mr./Ms.)", nomineeEntity.get(i).getFirstname());
					GuardianEntity guardianEntity = guardianRepository.findByNomineeId(nomineeEntity.get(i).getId());
					if (guardianEntity != null) {
						map.put("1stNDateOfbirth", nomineeEntity.get(i).getDateOfbirth());
						map.put("Details of 1st Nominee Name of Guardian", guardianEntity.getFirstname());
						if (guardianEntity.getAddress1() != null) {
							String addressOfgur = guardianEntity.getAddress1();
							map.put("Details of 1st Nominee Address of Guardian(s)",
									addressOfgur.substring(0, Math.min(26, addressOfgur.length())));
							if (addressOfgur.length() >= 26) {
								map.put("Details of 1st Nominee Address1 of Guardian(s)",
										addressOfgur.substring(26, Math.min(52, addressOfgur.length())));
							}
							if (addressOfgur.length() >= 52) {
								map.put("Details of 1st Nominee Address2 of Guardian(s)",
										addressOfgur.substring(52, Math.min(78, addressOfgur.length())));
							}

						}
						if (guardianEntity.getAddress2() != null) {
							String addressOfgur = guardianEntity.getAddress2();
							map.put("Details of 1st GNominee City / Place:",
									addressOfgur.substring(0, Math.min(26, addressOfgur.length())));
						}
						// map.put("Details of 1st Nominee Address of Guardian(s)",
						// guardianEntity.getAddress1());
						// map.put("Details of 1st GNominee City / Place:",
						// guardianEntity.getAddress2());
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
						if (guardianEntity.getEmailaddress() != null) {
							String emailOfgur = guardianEntity.getEmailaddress();
							map.put("1stNGEmailaddress", emailOfgur.substring(0, Math.min(26, emailOfgur.length())));
						}
						// map.put("1stNGEmailaddress", guardianEntity.getEmailaddress());
						map.put("Details of 1st Nominee Relatonship of Guardian with nominee",
								guardianEntity.getRelationship());
						if (guardianEntity.getPancard() != null) {
							map.put("1stNGPancardcheck", guardianEntity.getPancard());
							map.put("1stNGPancard", guardianEntity.getPancard());
						}
					}
				} else if (i == 1) {
					map.put("Details of  2nd Nominee Name of the nominee(s)", nomineeEntity.get(i).getFirstname());
					if (nomineeEntity.get(i).getAllocation() > 0) {
						map.put("Details of 2nd Nominee Share of each Nominee",
								Integer.toString(nomineeEntity.get(i).getAllocation()));
					} else {
						map.put("Details of 2nd Nominee Share of each Nominee", null);
					}
					map.put("Details of 2nd Nominee Relatonship with the Applicant (if any)",
							nomineeEntity.get(i).getRelationship());
					if (nomineeEntity.get(i).getAddress1() != null) {
						String addressOfNominee = nomineeEntity.get(i).getAddress1();
						map.put("Details of 2nd Nominee Address of Nominee(s)",
								addressOfNominee.substring(0, Math.min(26, addressOfNominee.length())));
						if (addressOfNominee.length() >= 26) {
							map.put("Details of 2nd Nominee Address1 of Nominee(s)",
									addressOfNominee.substring(26, Math.min(52, addressOfNominee.length())));
						}
						if (addressOfNominee.length() >= 52) {
							map.put("Details of 2nd Nominee Address2 of Nominee(s)",
									addressOfNominee.substring(52, Math.min(78, addressOfNominee.length())));
						}

					}
					if (nomineeEntity.get(i).getAddress2() != null) {
						String addressOfNominee = nomineeEntity.get(i).getAddress2();
						map.put("Details of 2nd Nominee City / Place:",
								addressOfNominee.substring(0, Math.min(26, addressOfNominee.length())));
					}
					// map.put("Details of 2nd Nominee Address of Nominee(s)",
					// nomineeEntity.get(i).getAddress1());
					// map.put("Details of 2nd Nominee City / Place:",
					// nomineeEntity.get(i).getAddress2());
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
					if (nomineeEntity.get(i).getEmailaddress() != null) {
						String emailOfNominee = nomineeEntity.get(i).getEmailaddress();
						map.put("2ndNEmailaddress", emailOfNominee.substring(0, Math.min(26, emailOfNominee.length())));
					}
					// map.put("2ndNEmailaddress", nomineeEntity.get(i).getEmailaddress());
					map.put("Signature2ndNFirstname", nomineeEntity.get(i).getFirstname());
					map.put("Name(s) of Holder(s) Second Holder (Mr./Ms.)", nomineeEntity.get(i).getFirstname());
					map.put("2ndNPancard", nomineeEntity.get(i).getPancard());
					GuardianEntity guardianEntity = guardianRepository.findByNomineeId(nomineeEntity.get(i).getId());
					if (guardianEntity != null) {
						map.put("2ndNDateOfbirth", nomineeEntity.get(i).getDateOfbirth());
						map.put("Details of 2nd Nominee Name of Guardian", guardianEntity.getFirstname());
						if (guardianEntity.getAddress1() != null) {
							String addressOfgur = guardianEntity.getAddress1();
							map.put("Details of 2nd Nominee Address of Guardian(s)",
									addressOfgur.substring(0, Math.min(26, addressOfgur.length())));
							if (addressOfgur.length() >= 26) {
								map.put("Details of 2nd Nominee Address1 of Guardian(s)",
										addressOfgur.substring(26, Math.min(52, addressOfgur.length())));
							}
							if (addressOfgur.length() >= 52) {
								map.put("Details of 2nd Nominee Address2 of Guardian(s)",
										addressOfgur.substring(52, Math.min(78, addressOfgur.length())));
							}

						}
						if (guardianEntity.getAddress2() != null) {
							String addressOfgur = guardianEntity.getAddress2();
							map.put("Details of 2nd Nominee City / Place of Guardian(s):",
									addressOfgur.substring(0, Math.min(26, addressOfgur.length())));
						}
						// map.put("Details of 2nd Nominee Address of Guardian(s)",
						// guardianEntity.getAddress1());
						// map.put("Details of 2nd Nominee City / Place of Guardian(s):",
						// guardianEntity.getAddress2());
						map.put("Details of 2nd Nominee State & Country of Guardian(s):", guardianEntity.getState());
						if (guardianEntity.getPincode() != null) {
							map.put("Details of 2nd Nominee PIN Code of Guardian(s)",
									guardianEntity.getPincode().toString());
						} else {
							map.put("Details of 2nd Nominee PIN Code of Guardian(s)", null);
						}
						if (guardianEntity.getEmailaddress() != null) {
							String emailOfgur = guardianEntity.getEmailaddress();
							map.put("2ndNGEmailaddress", emailOfgur.substring(0, Math.min(26, emailOfgur.length())));
						}
						// map.put("2ndNGEmailaddress", guardianEntity.getEmailaddress());
						map.put("Details of 2nd Nominee Relatonship of Guardian with nominee",
								guardianEntity.getRelationship());
						map.put("2ndNGPancard", guardianEntity.getPancard());
						if (guardianEntity.getMobilenumber() > 0) {
							map.put("2ndNGMobilenumber", Long.toString(guardianEntity.getMobilenumber()));
						} else {
							map.put("2ndNGMobilenumber", null);
						}
						if (guardianEntity.getPancard() != null) {
							map.put("1stNGPancardcheck", guardianEntity.getPancard());
							map.put("2ndNGPancard", guardianEntity.getPancard());
						}
					}
				} else if (i == 2) {
					map.put("Details of 3rd Nominee Name of the nominee(s)", nomineeEntity.get(i).getFirstname());
					if (nomineeEntity.get(i).getAllocation() > 0) {
						map.put("Details of 3rd Nominee Share of each Nominee",
								Integer.toString(nomineeEntity.get(i).getAllocation()));
					} else {
						map.put("Details of 3rd Nominee Share of each Nominee", null);
					}
					map.put("Details of 3rd Nominee Relatonship with the Applicant (if any)",
							nomineeEntity.get(i).getRelationship());
					if (nomineeEntity.get(i).getAddress1() != null) {
						String addressOfNominee = nomineeEntity.get(i).getAddress1();
						map.put("Details of 3rd Nominee Address of Nominee(s)",
								addressOfNominee.substring(0, Math.min(26, addressOfNominee.length())));
						if (addressOfNominee.length() >= 26) {
							map.put("Details of 3rd Nominee Address1 of Nominee(s)",
									addressOfNominee.substring(26, Math.min(52, addressOfNominee.length())));
						}
						if (addressOfNominee.length() >= 52) {
							map.put("Details of 3rd Nominee Address2 of Nominee(s)",
									addressOfNominee.substring(52, Math.min(78, addressOfNominee.length())));
						}
					}
					if (nomineeEntity.get(i).getAddress2() != null) {
						String addressOfNominee = nomineeEntity.get(i).getAddress2();
						map.put("Details of 3rd Nominee City / Place:",
								addressOfNominee.substring(0, Math.min(26, addressOfNominee.length())));
					}
					if (nomineeEntity.get(i).getEmailaddress() != null) {
						String emailOfNominee = nomineeEntity.get(i).getEmailaddress();
						map.put("3rdNEmailaddress", emailOfNominee.substring(0, Math.min(26, emailOfNominee.length())));
					}
					// map.put("Details of 3rd Nominee Address of Nominee(s)",
					// nomineeEntity.get(i).getAddress1());
					// map.put("Details of 3rd Nominee City / Place:",
					// nomineeEntity.get(i).getAddress2());
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
					// map.put("3rdNEmailaddress", nomineeEntity.get(i).getEmailaddress());
					map.put("Signature3rdNFirstname", nomineeEntity.get(i).getFirstname());
					map.put("Name(s) of Holder(s) Third Holder (Mr./Ms.)", nomineeEntity.get(i).getFirstname());
					map.put("3rdNPancard", nomineeEntity.get(i).getPancard());
					GuardianEntity guardianEntity = guardianRepository.findByNomineeId(nomineeEntity.get(i).getId());
					if (guardianEntity != null) {
						map.put("3rdNDateOfbirth", nomineeEntity.get(i).getDateOfbirth());
						map.put("Details of 3rd Nominee Name of Guardian", guardianEntity.getFirstname());
						if (guardianEntity.getAddress1() != null) {
							String addressOfgur = guardianEntity.getAddress1();
							map.put("Details of 3rd Nominee Address of Guardian(s)",
									addressOfgur.substring(0, Math.min(26, addressOfgur.length())));
							if (addressOfgur.length() >= 26) {
								map.put("Details of 3rd Nominee Address1 of Guardian(s)",
										addressOfgur.substring(26, Math.min(52, addressOfgur.length())));
							}
							if (addressOfgur.length() >= 52) {
								map.put("Details of 3rd Nominee Address2 of Guardian(s)",
										addressOfgur.substring(52, Math.min(78, addressOfgur.length())));
							}

						}
						if (guardianEntity.getAddress2() != null) {
							String addressOfgur = guardianEntity.getAddress2();
							map.put("Details of 3rd GNominee City / Place:",
									addressOfgur.substring(0, Math.min(26, addressOfgur.length())));
						}
						// map.put("Details of 3rd Nominee Address of Guardian(s)",
						// guardianEntity.getAddress1());
						// map.put("Details of 3rd GNominee City / Place:",
						// guardianEntity.getAddress2());
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
						if (guardianEntity.getEmailaddress() != null) {
							String emailOfgur = guardianEntity.getEmailaddress();
							map.put("3rdNGEmailaddress", emailOfgur.substring(0, Math.min(26, emailOfgur.length())));
						}
						// map.put("3rdNGEmailaddress", guardianEntity.getEmailaddress());

						map.put("3rdNGLastname", guardianEntity.getLastname());
						if (guardianEntity.getNomineeId() != null) {
							map.put("3rdNGNomineeId", guardianEntity.getNomineeId().toString());
						} else {
							map.put("3rdNGNomineeId", null);
						}
						if (guardianEntity.getPancard() != null) {
							map.put("1stNGPancardcheck", guardianEntity.getPancard());
							map.put("3rdNGPancard", guardianEntity.getPancard());
						}
						map.put("Details of 3rd Nominee Relatonship of Guardian with nominee",
								guardianEntity.getRelationship());
						map.put("3rdNGRelationship", guardianEntity.getRelationship());

					}
				}

			}
		}
		return map;
	}

	/**
	 * Method to generate Esign
	 */
	@Override
	public ResponseModel generateEsign(PdfApplicationDataModel pdfModel) {
		ResponseModel model = null;
		Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(pdfModel.getApplicationNo());
		if (userEntity.isPresent() && userEntity.get().getPdfGenerated() <= 0) {
			savePdf(pdfModel.getApplicationNo());
		}
		model = esign.runMethod(props.getFileBasePath(), pdfModel.getApplicationNo());
		return model;
	}

	/**
	 * Method to re direct from NSDL
	 */
	@Override
	public Response getNsdlXml(String msg) {
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		try {
			int random = commonMethods.generateOTP(9876543210l);
			String fileName = "lastXml" + random + ".xml";
			String cerFile = "usrCertificate" + random + ".cer";
			File fXmlFile = new File(props.getFileBasePath() + "TempXMLFiles" + slash + fileName);
			if (fXmlFile.createNewFile()) {
				FileWriter myWriter = new FileWriter(fXmlFile);
				myWriter.write(msg);
				myWriter.close();
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				Element eElement = doc.getDocumentElement();
				String txnName = eElement.getAttribute("txn");
				String errorMessage = eElement.getAttribute("errMsg");
				String errorCode = eElement.getAttribute("errCode");
				TxnDetailsEntity detailsEntity = txnDetailsRepository.findBytxnId(txnName);
				if (detailsEntity != null && detailsEntity.getApplicationId() != null
						&& detailsEntity.getApplicationId() > 0) {
					File nameFile = new File(detailsEntity.getFolderLocation() + slash + cerFile);
					if (nameFile.createNewFile()) {
						JSONObject xmlJSONObj = XML.toJSONObject(msg);
						String userCertificate = parseNSDLNameDetails(xmlJSONObj);
						if (StringUtil.isNotNullOrEmpty(userCertificate)) {
							FileWriter nameWriter = new FileWriter(nameFile);
							nameWriter.append("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator"));
							nameWriter.append(userCertificate + System.getProperty("line.separator"));
							nameWriter.append("-----END CERTIFICATE-----");
							nameWriter.close();
						}
					}
					String name = commonMethods
							.readUserNameFromCerFile(detailsEntity.getFolderLocation() + slash + cerFile);
					Optional<ApplicationUserEntity> userEntity = applicationUserRepository
							.findById(detailsEntity.getApplicationId());
					if (userEntity.isPresent()) {
						if (txnName != null && errorMessage != null && errorCode != null && !errorMessage.isEmpty()
								&& !errorCode.isEmpty() && errorMessage.equalsIgnoreCase("NA")
								&& errorCode.equalsIgnoreCase("NA")) {
							String filePath = detailsEntity.getFolderLocation();
							AddressEntity entity = addressRepository.findByapplicationId(userEntity.get().getId());
							String resposne = esign.getSignFromNsdl(
									props.getFileBasePath() + detailsEntity.getApplicationId() + slash
											+ userEntity.get().getPanNumber() + EkycConstants.PDF_EXTENSION,
									filePath, msg,
									StringUtil.isNotNullOrEmpty(name) ? name : userEntity.get().getUserName(),
									entity.getIsdigi() == 1 ? entity.getState() : entity.getKraCity(),
									userEntity.get().getId());
							if (StringUtil.isNotNullOrEmpty(resposne)) {
								String esignedFileName = userEntity.get().getPanNumber() + "_signedFinal"
										+ EkycConstants.PDF_EXTENSION;
								String path = filePath + slash + esignedFileName;
								applicationUserRepository.updateEsignStage(detailsEntity.getApplicationId(),
										EkycConstants.EKYC_STATUS_ESIGN_COMPLETED,
										EkycConstants.PAGE_COMPLETED_EMAIL_ATTACHED, 1, 1,
										StringUtil.isNotNullOrEmpty(name) ? name : userEntity.get().getUserName());
								saveEsignDocumntDetails(userEntity.get().getId(), path, esignedFileName);
								java.net.URI finalPage = new java.net.URI(EkycConstants.SITE_URL_FILE);
								// public void sendEsignedMail(String mailIds, String name, String filePath,
								// String fileName)
								commonMethods.sendEsignedMail(userEntity.get().getEmailId(),
										userEntity.get().getUserName(), path, esignedFileName);
								Response.ResponseBuilder responseBuilder = Response
										.status(Response.Status.MOVED_PERMANENTLY).location(finalPage);
								return responseBuilder.build();
							} else {

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occurred: " + e.getMessage());
			// commonMethods.SaveLog(applicationId, "PdfService", "getNsdlXml",
			// e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request. In getNsdlXml for the Error: " + e.getMessage(),
					"ERR-001");
		}

		return null;
	}

	public void saveEsignDocumntDetails(long applicationId, String documentPath, String fileName) {
		DocumentEntity oldEntity = docrepository.findByApplicationIdAndDocumentType(applicationId,
				EkycConstants.DOC_ESIGN);
		if (oldEntity == null) {
			DocumentEntity documentEntity = new DocumentEntity();
			documentEntity.setApplicationId(applicationId);
			documentEntity.setAttachementUrl(documentPath);
			documentEntity.setAttachement(fileName);
			documentEntity.setDocumentType(EkycConstants.DOC_ESIGN);
			documentEntity.setTypeOfProof(EkycConstants.DOC_ESIGN);
			docrepository.save(documentEntity);
		} else {
			oldEntity.setAttachementUrl(documentPath);
			oldEntity.setAttachement(fileName);
			docrepository.save(oldEntity);
		}
	}

	private static String parseNSDLNameDetails(JSONObject xmlJSONObj) {
		String response = "";
		try {
			if (xmlJSONObj != null) {
				if (xmlJSONObj.has("EsignResp")) {
					JSONObject sEnvelope = xmlJSONObj.getJSONObject("EsignResp");
					if (sEnvelope.has("UserX509Certificate")) {
						response = sEnvelope.getString("UserX509Certificate");
						return response;
					}
				} else {
					response = null;
				}
			} else {
				response = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}
}

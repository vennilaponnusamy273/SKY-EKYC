package in.codifi.api.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.DigioEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.model.DigioSaveAddResponse;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.DigioRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.restservice.DigioRestService;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class DigioHelper {
	private static String OS = System.getProperty("os.name").toLowerCase();
	@Inject
	AddressRepository addressRepository;
	@Inject
	DigioRestService digioRestService;
	@Inject
	DigioRepository digioRepository;
	@Inject
	DocumentHelper documentHelper;
	@Inject
	ApplicationProperties props;
	@Inject
	DocumentRepository documentRepository;

//	public ResponseModel saveAddFromDigio(long applicationId, DigioSaveAddResponse addResponse) {
//		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
//		if (OS.contains(EkycConstants.OS_WINDOWS)) {
//			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
//		}
//		System.out.println("the whDigilocker 6");
//		ResponseModel responseModel = new ResponseModel();
//		AddressEntity updatedAddEntity = null;
//		DigioEntity digioEntity = digioRepository.findByapplicationId(applicationId);
//		if (digioEntity != null) {
//			digioEntity.setXmlrequestId(addResponse.getActions().get(0).getExecutionRequestId());
//			digioRepository.save(digioEntity);
//			digioRestService.getXml(addResponse.getActions().get(0).getExecutionRequestId(), applicationId);
//			//digioRestService.savePANXmlDocument(addResponse.getActions().get(0).getExecutionRequestId(), applicationId);
//		}
//		System.out.println("the whDigilocker 7");
//		AddressEntity checkExit = addressRepository.findByapplicationId(applicationId);
//		if (checkExit == null && addResponse != null) {
//			System.out.println("the whDigilocker 7");
//			AddressEntity entity = new AddressEntity();
//			entity.setApplicationId(applicationId);
//			entity.setIsdigi(1);
//			entity.setIsKra(0);
//			entity.setAccessToken(addResponse.getId());
//			if (StringUtil.isListNotNullOrEmpty(addResponse.getActions())) {
//				entity.setAadharNo(addResponse.getActions().get(0).getDetails().getAadhaar().getIdNumber());
//				if (addResponse.getActions().get(0).getDetails() != null
//						&& addResponse.getActions().get(0).getDetails().getAadhaar() != null) {
//					if (StringUtil
//							.isNotNullOrEmpty(addResponse.getActions().get(0).getDetails().getAadhaar().getImage())) {
//						String fileName = documentHelper.convertBase64ToImage(
//								addResponse.getActions().get(0).getDetails().getAadhaar().getImage(), applicationId,
//								EkycConstants.IMAGE_NAME);
//						saveAadharDocumntDetails(applicationId, fileName,
//								props.getFileBasePath() + slash + applicationId + slash + fileName);
//					}
//					if (addResponse.getActions().get(0).getDetails().getAadhaar().getCurrentAddressDetails() != null) {
//						entity.setDigiCurAddress(sanitizeString(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getAddress()));
//						entity.setDigiCurLocality(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getLocalityOrPostOffice());
//						entity.setDigiCurDistrict(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getDistrictOrCity());
//						entity.setDigiCurState(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getState());
//						entity.setDigiCurPincode(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getPincode());
//					}
//					System.out.println("the whDigilocker 8");
//					if (addResponse.getActions().get(0).getDetails().getAadhaar()
//							.getPermanentAddressDetails() != null) {
//						System.out.println("the whDigilocker 9");
//						entity.setDigiPerAddress(sanitizeString(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getAddress()));
//						entity.setDigiPerLocality(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getLocalityOrPostOffice());
//						entity.setDigiPerDistrict(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getDistrictOrCity());
//						entity.setDigiPerState(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getState());
//						entity.setDigiPerPincode(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getPincode());
//						entity.setDigidob(addResponse.getActions().get(0).getDetails().getAadhaar().getDob());
//						entity.setDiginame(addResponse.getActions().get(0).getDetails().getAadhaar().getName());
//						entity.setDigigender(addResponse.getActions().get(0).getDetails().getAadhaar().getGender());
//					}
//				}
//				updatedAddEntity = addressRepository.save(entity);
//			}
//		} else {
//			checkExit.setApplicationId(applicationId);
//			checkExit.setIsdigi(1);
//			checkExit.setIsKra(0);
//			checkExit.setAccessToken(addResponse.getId());
//			System.out.println("the whDigilocker 10");
//			if (StringUtil.isListNotNullOrEmpty(addResponse.getActions())) {
//				if (addResponse.getActions().get(0).getDetails() != null
//						&& addResponse.getActions().get(0).getDetails().getAadhaar() != null) {
//					checkExit.setAadharNo(addResponse.getActions().get(0).getDetails().getAadhaar().getIdNumber());
//					if (StringUtil
//							.isNotNullOrEmpty(addResponse.getActions().get(0).getDetails().getAadhaar().getImage())) {
//						String fileName = documentHelper.convertBase64ToImage(
//								addResponse.getActions().get(0).getDetails().getAadhaar().getImage(), applicationId,
//								EkycConstants.IMAGE_NAME);
//						saveAadharDocumntDetails(applicationId, fileName,
//								props.getFileBasePath() + slash + applicationId + slash + fileName);
//					}
//					if (addResponse.getActions().get(0).getDetails().getAadhaar().getCurrentAddressDetails() != null) {
//						checkExit.setDigiCurAddress(sanitizeString(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getAddress()));
//						checkExit.setDigiCurLocality(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getLocalityOrPostOffice());
//						checkExit.setDigiCurDistrict(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getDistrictOrCity());
//						checkExit.setDigiCurState(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getState());
//						checkExit.setDigiCurPincode(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getCurrentAddressDetails().getPincode());
//					}
//					if (addResponse.getActions().get(0).getDetails().getAadhaar()
//							.getPermanentAddressDetails() != null) {
//						checkExit.setDigiPerAddress(sanitizeString(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getAddress()));
//						checkExit.setDigiPerLocality(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getLocalityOrPostOffice());
//						checkExit.setDigiPerDistrict(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getDistrictOrCity());
//						checkExit.setDigiPerState(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getState());
//						checkExit.setDigiPerPincode(addResponse.getActions().get(0).getDetails().getAadhaar()
//								.getPermanentAddressDetails().getPincode());
//						checkExit.setDigidob(addResponse.getActions().get(0).getDetails().getAadhaar().getDob());
//						checkExit.setDiginame(addResponse.getActions().get(0).getDetails().getAadhaar().getName());
//						checkExit.setDigigender(addResponse.getActions().get(0).getDetails().getAadhaar().getGender());
//						System.out.println("the whDigilocker 11");
//					}
//				}
//				updatedAddEntity = addressRepository.save(checkExit);
//			}
//		}
//		if (updatedAddEntity != null) {
//			responseModel.setResult(updatedAddEntity);
//			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
//			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
//		} else {
//			responseModel.setStat(EkycConstants.FAILED_STATUS);
//			responseModel.setMessage(EkycConstants.FAILED_MSG);
//		}
//		return responseModel;
//	}
	
	
	
	public ResponseModel saveAddFromDigio(long applicationId, DigioSaveAddResponse addResponse) throws JAXBException {
	    String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
	    if (OS.contains(EkycConstants.OS_WINDOWS)) {
	        slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
	    }

	    ResponseModel responseModel = new ResponseModel();
	    AddressEntity updatedAddEntity = null;
	    DigioEntity digioEntity = digioRepository.findByapplicationId(applicationId);
	    String xmlContent = null;

	    if (digioEntity != null) {
	        digioEntity.setXmlrequestId(addResponse.getActions().get(0).getExecutionRequestId());
	        digioRepository.save(digioEntity);
	        xmlContent = digioRestService.getXml(addResponse.getActions().get(0).getExecutionRequestId(), applicationId);
	    }

	    if (StringUtil.isNotNullOrEmpty(addResponse.getActions().get(0).getDetails().getAadhaar().getImage())) {
	        String fileName = documentHelper.convertBase64ToImage(
	            addResponse.getActions().get(0).getDetails().getAadhaar().getImage(), applicationId,
	            EkycConstants.IMAGE_NAME);
	        saveAadharDocumntDetails(applicationId, fileName,
	            props.getFileBasePath() + slash + applicationId + slash + fileName);
	    }

	    AddressEntity addressEntity = addressRepository.findByapplicationId(applicationId);
	    if (addressEntity == null) {
	        addressEntity = new AddressEntity();
	        addressEntity.setApplicationId(applicationId);
	        addressEntity.setAccessToken(addResponse.getId());
	    }

	    try {
	        // Initialize DocumentBuilderFactory
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();

	        // Parse the XML
	        Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
	        document.getDocumentElement().normalize();

	        // Extract UidData details
	        NodeList uidDataNodes = document.getElementsByTagName("UidData");
	        if (uidDataNodes.getLength() > 0) {
	            Element uidDataElement = (Element) uidDataNodes.item(0);
	            String uid = uidDataElement.getAttribute("uid");
	            addressEntity.setAadharNo(uid);
	        }

	        // Extract POA (Proof of Address) details
	        NodeList poaNodes = document.getElementsByTagName("Poa");
	        if (poaNodes.getLength() > 0) {
	            Element poaElement = (Element) poaNodes.item(0);
	            String co = poaElement.getAttribute("co");
	            String house = poaElement.getAttribute("house");
	            String street = poaElement.getAttribute("street");
	            String vtc = poaElement.getAttribute("vtc");
	            String sublist = poaElement.getAttribute("subdist");
	            String dist = poaElement.getAttribute("dist");
	            String state = poaElement.getAttribute("state");
	            String country = poaElement.getAttribute("country");
	            String pincode = poaElement.getAttribute("pc");
	            String loc = poaElement.getAttribute("po");

	            String address = buildAddress(co, house, street, vtc,loc, sublist);
	            System.out.println(address);
	            addressEntity.setDigiCurAddress(address);
	            addressEntity.setDigiPerAddress(address);
	            addressEntity.setDigiCurLocality(loc);
	            addressEntity.setDigiCurDistrict(dist);
	            addressEntity.setDigiCurState(state);
	            addressEntity.setDigiCurPincode(pincode);
	            addressEntity.setDigiPerLocality(loc);
	            addressEntity.setDigiPerDistrict(dist);
	            addressEntity.setDigiPerState(state);
	            addressEntity.setDigiPerPincode(pincode);
	            addressEntity.setDigiCurCountry(country);
	            addressEntity.setDigiPerCountry(country);
	        }

	        // Extract Poi (Proof of Identity) details
	        NodeList poiNodes = document.getElementsByTagName("Poi");
	        if (poiNodes.getLength() > 0) {
	            Element poiElement = (Element) poiNodes.item(0);
	            String dob = poiElement.getAttribute("dob");
	            String name = poiElement.getAttribute("name");
	            String gender = poiElement.getAttribute("gender");

	            addressEntity.setDigidob(dob);
	            addressEntity.setDiginame(name);
	            addressEntity.setDigigender(gender);
	        }

	        addressEntity.setIsdigi(1);
	        addressEntity.setIsKra(0);
	        updatedAddEntity = addressRepository.save(addressEntity);
	    } catch (ParserConfigurationException | SAXException | IOException e) {
	        e.printStackTrace(); // Consider using a logging framework here
	    }

	    if (updatedAddEntity != null) {
	        responseModel.setResult(updatedAddEntity);
	        responseModel.setStat(EkycConstants.SUCCESS_STATUS);
	        responseModel.setMessage(EkycConstants.SUCCESS_MSG);
	    } else {
	        responseModel.setStat(EkycConstants.FAILED_STATUS);
	        responseModel.setMessage(EkycConstants.FAILED_MSG);
	    }
	    return responseModel;
	}
	 public static String buildAddress(String... parts) {
	        List<String> nonNullParts = new ArrayList<>();

	        for (String part : parts) {
	            if (part != null && !part.trim().isEmpty()) {
	                nonNullParts.add(part);
	            }
	        }

	        return String.join(", ", nonNullParts);
	    }
	 
	 
	public static String sanitizeString(String input) {
	    // Define the pattern for allowed characters
	    String allowedCharactersPattern = "[^0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ&()_\\-\\[\\]\\\\,./ ]+";
	    // Replace non-allowed characters with an empty string
	    return input.replaceAll(allowedCharactersPattern, "");
	}
	public void saveAadharDocumntDetails(long applicationId, String fileName, String documentPath) {
		DocumentEntity oldEntity = documentRepository.findByApplicationIdAndDocumentType(applicationId,
				EkycConstants.DOC_AADHAR);
		if (oldEntity == null) {
			DocumentEntity documentEntity = new DocumentEntity();
			documentEntity.setApplicationId(applicationId);
			documentEntity.setAttachementUrl(documentPath);
			documentEntity.setAttachement(fileName);
			documentEntity.setDocumentType(EkycConstants.DOC_AADHAR);
			documentEntity.setTypeOfProof(EkycConstants.DOC_AADHAR);
			documentRepository.save(documentEntity);
		} else {
			oldEntity.setAttachementUrl(documentPath);
			oldEntity.setAttachement(fileName);
			documentRepository.save(oldEntity);
		}
	}
}

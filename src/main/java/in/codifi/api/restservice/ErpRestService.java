package in.codifi.api.restservice;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.entity.PennyDropEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.model.BankAddressModel;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.repository.PennyDropRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.utilities.CommonMethods;
@ApplicationScoped
public class ErpRestService {

	@Inject
	@RestClient
	IerpRestService ierpRestService;
	@Inject
	ApplicationProperties props;
	@Inject
	ApplicationUserRepository repository;
	@Inject
	AddressRepository addressRepository;
	@Inject
	ProfileRepository profileRepository;
	@Inject 
	CommonMethods commonMethods;
	@Inject
	RazorpayIfscRestService commonRestService;
	@Inject
	PennyDropRepository pennyDropRepository;
	@Inject
	NomineeRepository nomineeRepository;
	
	@Inject
	BankRepository bankRepository;
	/**
	 * Method to get ERP details
	 * 
	 * @author Nila
	 * @param otp
	 * @param mobile Number
	 * @return
	 */
	
	 public String UserCreation(long mobileNo, String userId, String emailId, String password) {
	        String message = null;
	        try {
	            String authorizationHeader = "token "+props.getUserCreationauthToken();
	            String requestBody = String.format("{\"mobile_no\": \"%s\", \"user_id\": \"%s\",\"email_id\":\"%s\",\"password\":\"%s\"}", mobileNo, userId, emailId, password);
	            message = ierpRestService.createUser(authorizationHeader, requestBody);
	            System.out.println("Message: " + message);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return message;
	    }
	 public String uploadDocument(String documentType, String userId, String base64Content) {
		 String message = null;
		    try {
		    	String authorizationHeader = "token "+props.getUserCreationauthToken();
		        String requestData = "{\"user_id\":\"" + userId + "\",\"document_type\":\"" + documentType + "\"}";
		        message  = ierpRestService.uploadDocument(authorizationHeader, requestData, base64Content);
		        System.out.println("the message in doc"+message);
		    } catch (Exception e) {
		    	  e.printStackTrace();
		    }

		    return message;
		}
	 public String updatefulldetails(long  userId) {
		 String message = null;
		    try {
		    	String authorizationHeader = "token "+props.getUserCreationauthToken();
		    	Optional<ApplicationUserEntity> isUserPresent = repository.findById(userId);
		    	AddressEntity address = addressRepository.findByapplicationId(userId);
		    	String	address_1=null;
		    	String address_2=null;
		    	String state=null;
		    	long pincode = 0;
		    	SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
    	    	SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		    	if(address!=null) {
		    		address_1=(address.getKraPerAddress1()!=null)?address.getKraPerAddress1():address.getAddress1();
		    	 address_2=(address.getKraPerAddress2()!=null)?address.getKraAddress2():address.getAddress2();
		    	 state=(address.getKraPerState()!=null)?address.getKraPerState():address.getState();
		    	 pincode = (address.getKraPerPin() >0) ? address.getKraPerPin() : address.getPincode();
		    	}
		    	ProfileEntity profileEntity = profileRepository.findByapplicationId(userId);
		    	BankEntity bankDetails = bankRepository.findByapplicationId(userId);
		    	PennyDropEntity penny=pennyDropRepository.findByapplicationId(userId);
		    	if(bankDetails!=null) {
			    BankAddressModel model = commonRestService.getBankAddressByIfsc(bankDetails.getIfsc());
				if (model != null) {
		    	List<NomineeEntity> nomineeEntity = nomineeRepository.findByapplicationId(userId);
		    	String NomineeDetails=null;
		    	String finalNomineeDetails=null;
		    	String base64Path1=null;
		    	String base64Path2=null;
		    	String base64Path3=null;
		    	if (!nomineeEntity.isEmpty()) {
		    	    StringBuilder nomineeDetailsBuilder = new StringBuilder();
		    	    for (int i = 0; i < Math.min(nomineeEntity.size(), 3); i++) {
		    	    	String dob = nomineeEntity.get(i).getDateOfbirth();
		    	    	 Date date = inputFormat.parse(dob);
		    	    	 String formattedDate = outputFormat.format(date);
		    	        String nomineeDetails = "{\"first_name\":\"" + nomineeEntity.get(i).getFirstname() + "\",\"last_name\":\"" + nomineeEntity.get(i).getLastname() + "\",\"relationship\":\"" + nomineeEntity.get(i).getRelationship() + "\",\"dob\":\"" + formattedDate + "\",\"mobile_number\":\"" + nomineeEntity.get(i).getMobilenumber() + "\",\"pan_number\":" + nomineeEntity.get(i).getPancard() + ",\"email_id\":\"" + nomineeEntity.get(i).getEmailaddress() + "\",\"address_1\":\"" + nomineeEntity.get(i).getAddress1() + "\",\"address_2\":\"" + nomineeEntity.get(i).getAddress2() + "\",\"state\":\"" + nomineeEntity.get(i).getState() + "\",\"pincode\":" + nomineeEntity.get(i).getPincode() + "}";
		    	    /**	String nomineeDetails = "{\"first_name\":\"" + nomineeEntity.get(i).getFirstname() + "\","
		    	                + "\"last_name\":\"" + nomineeEntity.get(i).getLastname() + "\","
		    	                + "\"relationship\":\"" + nomineeEntity.get(i).getRelationship() + "\","
		    	                + "\"dob\":\"" + nomineeEntity.get(i).getDateOfbirth() + "\","
		    	                + "\"mobile_number\":\"" + nomineeEntity.get(i).getMobilenumber() + "\","
		    	                + "\"pan_number\":" + nomineeEntity.get(i).getPancard() + ","
		    	                + "\"email_id\":\"" + nomineeEntity.get(i).getEmailaddress() + "\","
		    	                + "\"address_1\":\"" + nomineeEntity.get(i).getAddress1() + "\","
		    	                + "\"address_2\":\"" + nomineeEntity.get(i).getAddress2() + "\","
		    	                + "\"state\":\"" + nomineeEntity.get(i).getState() + "\","
		    	                + "\"pincode\":" + nomineeEntity.get(i).getPincode() + "}";**/

		    	        if (i < nomineeEntity.size() - 1) {
		    	            nomineeDetailsBuilder.append(nomineeDetails).append(",");
		    	        } else {
		    	            nomineeDetailsBuilder.append(nomineeDetails);
		    	        }
		    	        List<String> base64Paths = new ArrayList<>();
		    	            NomineeEntity nominee = nomineeEntity.get(i);
		    	            String base64Path = "nominee" + (i + 1) + "_base64content";
		    	            String filePath = nominee.getAttachementUrl();
		    	            Path file = Paths.get(filePath);
		    	            byte[] fileBytes = Files.readAllBytes(file);
		    	            String base64String = Base64.getEncoder().encodeToString(fileBytes);
		    	            base64Paths.add(base64String);
		    	            if (i == 0) {
		    	                base64Path1 = base64String;
		    	            } else if (i == 1) {
		    	                base64Path2 = base64String;
		    	            } else if (i == 2) {
		    	                base64Path3 = base64String;
		    	            }
		    	    }
		    	     finalNomineeDetails = nomineeDetailsBuilder.toString();
		    	    System.out.println("The finalNomineeDetails: " + finalNomineeDetails);
		    	}
		    	String UserDob=isUserPresent.get().getDob();
		    	 Date Userdate = inputFormat.parse(UserDob);
    	    	 String formattedDateUser = outputFormat.format(Userdate);
		    	String data="{\"user_id\":\""+userId+"\",\"in_progress\":1,\"mobile_no\":\""+isUserPresent.get().getMobileNo()+"\",\"email_id\":\""+isUserPresent.get().getEmailId()+"\",\"mobile_otp\":"+isUserPresent.get().getSmsOtp()+",\"email_otp\":"+isUserPresent.get().getEmailOtp()+",\"date_of_birth\":\""+formattedDateUser+"\",\"pan_name\":\""+isUserPresent.get().getUserName()+"\",\"pswd\":\""+isUserPresent.get().getPassword()+"\",\"address_1\":\""+address_1+"\",\"address_2\":\""+address_2+"\",\"pan_number\":"+isUserPresent.get().getPanNumber()+",\"state\":\""+state+"\",\"country\":\"India\",\"pincode\":"+pincode+",\"father_name\":\""+profileEntity.getFatherName()+"\",\"mother_name\":\""+profileEntity.getMotherName()+"\",\"gender\":\""+profileEntity.getGender()+"\",\"trading_experience\":\""+profileEntity.getTradingExperience()+"\",\"occupation\":\""+profileEntity.getOccupation()+"\",\"political_exposed_person\":\""+profileEntity.getPoliticalExposure()+"\",\"unused_fund\":858,\"net_worth\":"+profileEntity.getNetWorth()+",\"annual_income\":\""+profileEntity.getAnnualIncome()+"\",\"actions_taken\":\""+profileEntity.getLegalAction()+"\",\"ifsc_code\":\""+bankDetails.getIfsc()+"\",\"acc_no\":"+bankDetails.getAccountNo()+",\"penny_drop_verification\":\""+penny.getConfirmPenny()+"\",\"branch_name\":\""+bankDetails.getBranchName()+"\",\"micr_code\":"+bankDetails.getMicr()+",\"add_of_bank\":\""+bankDetails.getAddress()+"\",\"bank\":\""+model.getBank()+"\",\"nominees\":["+finalNomineeDetails+"]}";
		    	/**String data = "{\"user_id\":\"" + userId + "\","
		                + "\"in_progress\":1,"
		                + "\"mobile_no\":\"" + isUserPresent.get().getMobileNo() + "\","
		                + "\"email_id\":\"" + isUserPresent.get().getEmailId() + "\","
		                + "\"mobile_otp\":" + isUserPresent.get().getSmsOtp() + ","
		                + "\"email_otp\":" + isUserPresent.get().getEmailOtp() + ","
		                + "\"date_of_birth\":\"" + isUserPresent.get().getDob() + "\","
		                + "\"pan_name\":\"" + isUserPresent.get().getUserName() + "\","
		                + "\"pswd\":\"" + isUserPresent.get().getPassword() + "\","
		                + "\"address_1\":\"" + address_1 + "\","
		                + "\"address_2\":\"" + address_2 + "\","
		                + "\"pan_number\":" + isUserPresent.get().getPanNumber() + ","
		                + "\"state\":\"" + state + "\","
		                + "\"country\":\"India\","
		                + "\"pincode\":" + pincode + ","
		                + "\"father_name\":\"" + profileEntity.getFatherName() + "\","
		                + "\"mother_name\":\"" + profileEntity.getMotherName() + "\","
		                + "\"gender\":\"" + profileEntity.getGender() + "\","
		                + "\"trading_experience\":\"" + profileEntity.getTradingExperience() + "\","
		                + "\"occupation\":\"" + profileEntity.getOccupation() + "\","
		                + "\"political_exposed_person\":\"" + profileEntity.getPoliticalExposure() + "\","
		                + "\"unused_fund\":858,"
		                + "\"net_worth\":" + profileEntity.getNetWorth() + ","
		                + "\"annual_income\":\"" + profileEntity.getAnnualIncome() + "\","
		                + "\"actions_taken\":\"" + profileEntity.getLegalAction() + "\","
		                + "\"ifsc_code\":\"" + bankDetails.getIfsc() + "\","
		                + "\"acc_no\":" + bankDetails.getAccountNo() + ","
		                + "\"penny_drop_verification\":\"" + penny.getConfirmPenny() + "\","
		                + "\"branch_name\":\"" + bankDetails.getBranchName() + "\","
		                + "\"micr_code\":" + bankDetails.getMicr() + ","
		                + "\"add_of_bank\":\"" + bankDetails.getAddress() + "\","
		                + "\"bank\":\"" + model.getBank() + "\","
		                + "\"nominees\":[" + finalNomineeDetails + "]}";**/

		        message  = ierpRestService.uploadAllDetails(authorizationHeader,data,base64Path1,base64Path2,base64Path3);
		        System.out.println("the message in doc"+message);
		    }}} catch (Exception e) {
		    	  e.printStackTrace();
		    }
		    return message;
		}
	 
	 
}

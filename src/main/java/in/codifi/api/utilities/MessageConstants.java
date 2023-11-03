package in.codifi.api.utilities;

public class MessageConstants {
	public static final String INTERNAL_SERVER_ERROR = "Something went wrong. please try again after some time";
	public static final String KEYCLOAK_EMAIL_EXIST = "EMAIL ID already exist in our database";
	public static final String KEYCLOAK_MOBILE_EXIST = "Mobile number already exist in our database";
	public static final String PARAMETER_NULL = "The Given Parameter is null";
	public static final String MOBILE_NUMBER_NULL = "The given Mobile Number is null";
	public static final String EMAIL_ID_NULL = "The given Email Id is null";
	public static final String RZ_EMAIL_NULL = "Email ID is Null";
	public static final String RZ_MOBILE_NUMBER_NULL = "Mobile Number is Null";
	public static final String PAN_NUMBER_NULL = "The given Pan Number is null";
	public static final String USER_ID_NULL = "The given user Id is null";
	public static final String INVLAID_PARAMETER = "The Given Parameter is Invalid";
	public static final String NULL_PASSWORD = "The Given password is Null";
	public static final String USER_ID_INVALID = "The given user Id is Invalid";
	public static final String ADDRESS_NOT_YET = "Address details not Yet";
	public static final String MOBILE_NUMBER_WRONG = "The given Mobile Number is wrong";
	public static final String EMAIL_ID_WRONG = "The given Email Id is wrong";
	public static final String EMAIL_ID_ALREADY_AVAILABLE = "The given email ID already exists. Please try again with different email ID.";
	public static final String PAN_ALREADY_AVAILABLE = "The given PAN Number is already available";
	public static final String ERROR_WHILE_GENERATE_OTP = "Error Occur While Generating OTP";
	public static final String ERROR_WHILE_VERIFY_OTP = "Error Occur While Verifying OTP";
	public static final String INVALID_OTP = "Invalid OTP";
	public static final String INVALID_OTP_TRY_AFTER = "The given otp is Invalid Try again after ";
	public static final String AGE_RESTRICTION = "You Are Minor";
	public static final String RETRY_OTP_TRY_AFTER = "Please request otp after ";
	public static final String SECONDS = " seconds";
	public static final String OTP_TIME_EXPIRED = "Your OTP Time Expired";
	public static final String SMS_OTP_NOT_VERIFIED = "Sms OTP Not Verified";
	public static final String WRONG_USER_ID = "User Id is Wrong";
	public static final String MANDATORY_FIELDS_NULL = "Mandatory Fileds are null";
	public static final String EKYC_ACTIVE_CUSTOMER = "An Account already exists with the Mobile Number.Please try with another Mobile Number";
	public static final String EKYC_EMAIL_ACTIVE_CUSTOMER = "An Account already exists with the Email ID.Please try with another Email ID";
	public static final String EKYC_DORMANT_CUSTOMER = "Your account is Dormant / Closed. Click to Proceed for Re-KYC";
	public static final String PAN_FILE_USAGE = "usage: java p2j certi.pfx pfxpswd oupt.jks";
	public static final String PAN_KEY_STORE_MSG = "Unable to access input keystore: ";
	public static final String PAN_FILE_NOT_WRITE = "Output file is not writable: ";
	public static final String PAN_KEYSTORE_SUC_MSG = "Java Key Store created successfully";
	public static final String PAN_PKCS7GEN = "java pkcs7gen output.jks pfxpswd dataToBeSigned oupt.sig";
	public static final String PAN_SIGN_OUT = "Signature file written to ";
	public static final String PAN_EXE_MSG = " ::Exception: ";
	public static final String PAN_PRG_SRT_TIME = " ::Program Start Time:";
	public static final String PAN_PRG_NO = " ::nonce= ";
	public static final String INVALID_PAN_MSG = "Invalid PAN. Please re-enter the valid PAN";
	public static final String ERROR_WHILE_SAVING_DOB = "Error Occur While Saving DOB";
	public static final String DIGI_CODE_NULL = "Digi Code is null";
	public static final String DIGI_STATE_NULL = "Digi State is null";
	public static final String FAILED_HTTP_CODE = "Failed : HTTP error code :";
	public static final String DIGI_SYSOUT_BR = "the br1:";
	public static final String ERR_NO_ACC_TOKEN = "No Access Token Generated From Digilocker";
	public static final String ERR_ACC_TOKEN = "Error while generate access Token";
	public static final String ERR_SAVE_DIGI = "Error while Saving address. Please contact system administrator";
	public static final String ERR_NULL_DIGI = "Response null from digilocker";
	public static final String ERROR_WHILE_SAVING_PROFILE = "Error Occur While Saving Profile";
	public static final String ERROR_WHILE_SAVING_BANK_DETAILS = "Error Occur While Saving Bank Details";
	public static final String USER_NOT_VERIFIED = "You are not verified Customer please reverified";
	public static final String IFSC_INVALID = "The given user IFSC Code is Invalid";
	public static final String PINCODE_INVALID = "The given pincode is Invalid";
	public static final String ACC_NUM_MISMATCH = "The given Account number are not same";
	public static final String NOMINEE_AVAILABLE = "Nominee Details already Available";
	public static final String ERROR_WHILE_SAVING_NOMINEE_DETAILS = "Error Occur While Saving Nominee Details";
	public static final String ERROR_WHILE_SAVING_SEGMENT_DETAILS = "Error Occur While Saving Segment Details";
	// Document
	public final static String FAILED_DOC_UPLOAD = "Failed While Upload Document";
	public final static String FAILED_IVR_DOC_UPLOAD = "Failed While IVR Upload Document";
	public final static String INVALID_IVR_PARAMS = "Invalid parameters in IVR Upload";
	public static final String CHECK_DOC_CON = "Check File Type and Size";
	public final static String IVR_IMAGE_NULL = "Ivr Image is null";
	public final static String IVR_LAT_NULL = "Latitude is null";
	public final static String IVR_LON_NULL = "Longitude is null";
	public final static String IVR_TOKEN_NULL = "Token is null";
	public final static String IVR_INVALID_TOKEN = "Invalid Token";
	public final static String INVALID_IVR_INVALID = "Your IVR Image is invalid";
	public final static String ERROR_LIVENESS = "Error while check liveness";
	public static final String LINK_TYPE_NULL = "The given type is null";
	// Payment
	public static final String AMOUNT_NULL = "Amount is Zero";
	public static final String RECEIPT_NULL = "Receipt is null";
	public static final String VERIFY_URL_NULL = "Verify URL is null";
	public static final String PAYMENT_CREATION_FAILED = "Payment Creation Failed Check Server!";
	public static final String NOT_FOUND_DATA = "No Record Found for this application ID";
	public static final String PAYMENT_ALREADY_CREATED = "Payment already Created";
	public static final String ERROR_WHILE_SAVE_CREATE_PAYMENT = "Error Occur While Saving Create Payment";
	public static final String ERROR_WHILE_SAVE_VERIFY_PAYMENT = "Error Occur While Saving Verify Payment";
	public static final String BANK_DETAILS_NULL = "Bank Details is null please create Bank Details";
	public static final String RAZORPAY_ORDER_ID_NULL = "Razorpay Order Id Null";
	public static final String RAZORPAY_PAYMENT_ID_NULL = "Razorpay Payment Id Null";
	public static final String RAZORPAY_SIGNATURE_NULL = "Razorpay Signature is Null";
	public static final String VERIFY_NOT_SUCCEED = "Verify Not Succeed";
	public static final String PAYMENT_ALREADY_COMPLETED = "Payment already Completed";
	public static final String PAYMENT_CREATED_COMPLETE_IT = "Payment Created. Please complete it";
	public static final String PAYMENT_NOT_CREATED = "Payment is not created";
	public static final String COMPLETE_PAYMENT_FIRST = "Please complete payment";
	// Nominee
	public static final String GUARDIAN_MSG = "No Need to Guardian for this Nominee ,otherwise Nominee details saved ";
	public final static String NOMINEE_COUNT = "This ApplicationID Against Three Nominee's  Allocated Done";
	public final static String GUARDIAN_REQUIRED = "Guardian Required For this Nominee";
	public final static String ALLOCATION_NOT_TALLY = "please give correct allocation";
	// KRA
	public final static String KRA_FAILED = "YOUR REQUEST COULD NOT BE PROCESSED.";
	public final static String PENNY_DETAILS_NULL = "Penny Drop Details null";
	public final static String PENNY_ACCOUNT_CREATED = "Already Contact and account Created";
	public final static String PENNY_DROP_NOT_PROCEED = "Penny Drop Cannot be proceed";
	public final static String PENNY_ALREADY_DONE = "Penny Drop already performed";
	public final static String PENNY_CONTACT_ID_NULL = "Contact Id is Null ";
	public final static String FUND_ACCOUNT_ID_NULL = "Fund account Id is Null ";
	public final static String ACCOUNT_IFSC_NULL = "Bank account number or IFSC is null";
	// BANK STATEMENT
	public static final String USER_DETAILS_INVALID = "The Given User Details is invalid";
	public static final String FILE_NULL = "please choose file";
	public static final String NOM_FILE_NULL = "Nominee proof empty";
	public static final String GUARD_FILE_NULL = "Guardian proof empty";
	// STATUS OK
	public static final String STATUS_OK = "OK";
	public static final String WRONG_DOCUMENT = "The given document type is wrong";

	public static final String FILE_NOT_FOUND = "File Not found on this ID";
	public static final String AADHAR_NOT_AVAILABLE = "Aadhaar data is not available for this user. Please perform Aadhaar eKYC again.";
	public static final String AADHAR_INTERNAL_SERVER_ERR = "Internal server error from Digilocker";
	public static final String AADHAR_TOKEN_400 = "Bad request from Digilocker";
	public static final String AADHAR_TOKEN_401 = "Token Expired from Digilocker";
	public static final String ERROR_WHILE_CREATING_XML = "Error Occur While Creating XML file";
	public static final String INVAILD_EMAILID_MOBILEBO = "Invalid EmailID and MobileNO";

	public static final String UPDATESTAGE_STATUS_MSG = "CHECK STATUS EAITHER ONLINE OR OFFINE";
	public static final String XML_MSG_NULL = "The XML MSG is Null";
	// TRACKWIZZ
	public final static String FAILED_TRACK_WIZZ = "Error Occured While chacking trackwizz";
	public static final String KEYVALUE_NOTFOUND = "Key value not found for the given page number.";
	// Nominee Opted Out
	public final static String NOMINEE_OPTED_OUT = "Nominee Opted out successfully";
	public final static String NOMINEE_OPTED_OUT_FAILED = "Failed while updating nominee Opted out";
	public static final String FAILED = "Failed";
	public static final String INVAILD_BROKERAGE_TYPE = "Brokerage type contains only sky prime or sky discount";
	public static final String ERROR_MSG_INVALID_PAN = "INVALID DOB (APP_DOB_INCORP) PROVIDED";
	
	// DIGIO
	public static final String DIGIO_REQ_FAILED = "Failed to construct DIGIO Request";
	public static final String DIGIO_INI_RES_NULL = "Digio Initailize Response null";
	public final static String GUARDIAN_REQUIRED_PROOF = "Guardian Proof is Required, because Nominee is minor";
	public static final String BANK_ID_NULL = "YOUR BANK ID IS NULL FOR ATOM , CONTACT SUPPORT TEAM";
}

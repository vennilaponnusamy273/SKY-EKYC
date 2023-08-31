package in.codifi.api.utilities;

public class EkycConstants {
	public static final int FAILED_STATUS = 0;
	public static final int SUCCESS_STATUS = 1;
	public static final String FAILED_MSG = "Failed";
	public static final String SUCCESS_MSG = "Success";
	// SMS constants
//	public static final String OTP_MSG = " is Your OTP for Registration with SKY COMMODITIES INDIA PVT. LTD";
	public static final String OTP_MSG = " is your OTP / verification code for Signup.-NIDHI";
	public static final String CONST_SMS_FEEDID = "feedid=";
	public static final String CONST_SMS_SENDERID = "senderid=";
	public static final String CONST_SMS_USERNAME = "username=";
	public static final String CONST_SMS_PASSWORD = "password=";
	public static final String CONST_SMS_TO = "To=";
	public static final String CONST_SMS_TEXT = "Text=";
	// HTTP
	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
	// Symbol
	public static final String AND = "&";
	public static final String UNDERSCORE = "_";
	public static final String CLIENT_CODE = "AB";
	public static final int count = 0;
	// OTP Validation
	public static final String SMS_KEY = "_SMS";
	public static final String EMAIL_KEY = "_EMAIL";
	public static final String IVR_SMS_KEY = "SMS";
	public static final String IVR_EMAIL_KEY = "EMAIL";
	// ERP
	public static final String ERP_MOBILE = "MOBILE";
	public static final String ERP_PAN = "PAN";
	public static final String ERP_EMAIL = "EMAIL";
	public static final String EXISTING_YES = "Yes";
	public static final String EXISTING_NO = "No";
	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_DORMANT = "DORMANT";
	public static final String STATUS_INACTIVE = "INACTIVE";
	public static final String EKYC_STATUS_COMPLETED = "Completed";
	public static final String EKYC_STATUS_INPROGRESS = "In-Progress";
	public static final String EKYC_STATUS_PENDING = "Pending";
	// PAGE STATUS
	public static final String PAGE_SMS = "0.5";
	public static final String PAGE_EMAIL = "1";
	public static final String PAGE_PASSWORD = "1.1";
	public static final String PAGE_PAN = "2";
	public static final String PAGE_PAN_NSDL_DATA_CONFIRM = "2.1";
	public static final String PAGE_PAN_CONFIRM = "2.2";
	public static final String PAGE_PAN_KRA_DOB_ENTRY = "2.3";
	public static final String PAGE_AADHAR = "3";
	public static final String PAGE_PROFILE = "4";
	public static final String PAGE_BANK = "5";
	public static final String PAGE_PENNY = "5.1";
	public static final String PAGE_SEGMENT = "6";
	public static final String PAGE_PAYMENT = "7";
	public static final String PAGE_NOMINEE = "8";
	public static final String PAGE_NOMINEE_1 = "8.1";
	public static final String PAGE_NOMINEE_2 = "8.2";
	public static final String PAGE_NOMINEE_3 = "8.3";
	public static final String PAGE_DOCUMENT = "9";
	public static final String PAGE_IPV = "10";
	public static final String PAGE_PDFDOWNLOAD = "11";
	public static final String PAGE_ESIGN = "12";
	public static final String PAGE_COMPLETED_EMAIL_ATTACHED = "13";
	public static final String UBUNTU_FILE_SEPERATOR = "/";
	public static final String WINDOWS_FILE_SEPERATOR = "\\\\";
	public static final String OS_WINDOWS = "win";
	// PAN
	public static final String FILE_JKS = ".jks";
	public static final String KEY_PKS = "pkcs12";
	public static final String KEY_JKS = "jks";
	public static final String OUTPUT_SIG = "output.sig";
	public static final String SIGN_COLL = "Collection";
	public static final String SIGN_BC = "BC";
	public static final int FILE_ARGS = 3;
	public static final String SSL = "SSL";
	public static final String LOG_MSG_REQ = "::Request Sent At: ";
	public static final String LOG_MSG_DATA = "::Request Data: ";
	public static final String LOG_MSG_VERSION = "::Version: ";
	public static final String CONN_OUTPUT = "Output: ";
	public static final String CONSTANT_CONTENT_TYPE = "Content-Type";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONSTANT_URL_ENCODED = "application/x-www-form-urlencoded";
	public static final String CONSTANT_APPLICATION_JSON = "application/json";
	public static final String CONSTANT_CONTENT_LENGTH = "Content-Length";
	public static final String CONSTANT_CONTENT_LANG = "Content-Language";
	public static final String LAG_ENG_US = "en-US";
	public static final String CONSTANT_URL_DATA = "data=";
	public static final String CONSTANT_URL_UF8 = "UTF-8";
	public static final String CONSTANT_VERSION = "&version=";
	public static final String CONSTANT_SIGNATURE = "&signature=";
	public static final String PAN_FIRSTNAME = "firstName";
	public static final String PAN_LASTNAME = "lastName";
	public static final String PAN_MIDDLENAME = "middleName";
	public static final String PAN_NAMEONCARD = "nameOnCard";
	public static final String PAN_AADHAR_STATUS = "aadhaar seeding status";
	public static final String PAN_LAST_UPDATED_DATE = "lastUpdatedDate";
	public static final String PAN_CARD = "panCard";
	public static final String PAN_CARD_STATUS = "panCardStatus";
	public static final String PAN_TITLE = "panTittle";
	public static final String CONSTANT_ERROR_MSG = "ERROR_MSG";
	public static final String CONSTANT_ERROR_DESC = "APP_ERROR_DESC";
	// Digi
	public static final String DIGI_CONST_AUTH_CLIENT_ID = "authorize?client_id=";
	public static final String DIGI_CONST_RES_TYPE = "&response_type=";
	public static final String DIGI_CONST_STATE = "&state=";
	public static final String DIGI_CONST_CODE = "code=";
	public static final String DIGI_CONST_GRANDTYPE_CLIENTID = "&grant_type=authorization_code&client_id=";
	public static final String DIGI_CONST_CLIENT_SECRET = "&client_secret=";
	public static final String DIGI_CONST_REDIRECT_URL = "&redirect_uri=";
	public static final String DIGI_CONST_ACCESS_TOKEN = "access_token";
	public static final String DIGI_CONST_TOKEN = "token";
	public static final String AUTH = "Authorization";
	public static final String BEARER_TOKEN = "Bearer ";
	// Doc Upload
	public static final String CONST_APPLICATION_PDF = "application/pdf";
	public static final String PDF_EXTENSION = ".pdf";
	public static final String XML_EXTENSION = ".xml";
	public static final String DOC_IVR = "IPV";
	public static final String DOC_CHEQUE = "CANCELLED_CHEQUE_OR_STATEMENT";
	public static final String DOC_INCOME = "INCOME_PROOF";
	public static final String DOC_SIGNATURE = "SIGNATURE";
	public static final String DOC_ESIGN = "ESIGN_DOCUMENT";
	public static final String DOC_AADHAR = "AADHAR_IMAGE";
	public static final String DOC_PAN = "PAN";
	public static final String DOC_PAN_ERP = "Pan Card";
	public static final String DOC_PHOTO = "Photo";
	// Razorpay
	public static final String AMOUNT = "amount";
	public static final String CURRENCY = "currency";
	public static final String RECEIPT = "receipt";
	public static final String RAZORPAY_CURRENCY_INR = "INR";
	public static final String CONST_BANK_ACCOUNT = "bank_account";
	public static final String CONST_BANK_ACCOUNT_NUMBER = "account_number";
	public static final String CONST_BANK_NAME = "name";
	public static final String CONST_BANK_IFSC = "ifsc";
	public static final String RAZORPAY_ORDERID = "razorpay_order_id";
	public static final String RAZORPAY_PAYMENTID = "razorpay_payment_id";
	public static final String RAZORPAY_SIGNATURE = "razorpay_signature";
	public static final String RAZORPAY_STATUS_ = "";
	public static final String RAZORPAY_STATUS_COMPLETED = "completed";
	public static final String RAZORPAY_STATUS_CREATED = "created";
	public static final String CONST_NAME = "name";
	public static final String CONST_EMAIL = "email";
	public static final String CONST_CONTACT = "contact";
	public static final String CONST_ACTIVE = "active";
	public static final String CONST_CUSTOMER = "customer";
	public static final String CONST_REFERENCE_ID = "reference_id";
	public static final String CONST_CONATCT_ID = "contact_id";
	public static final String CONST_IFSC = "ifsc";
	public static final String CONST_NOTES_1 = "notes_key_1";
	public static final String CONST_NOTES_2 = "notes_key_2";
	public static final String CONST_TYPE = "type";
	public static final String CONST_ROLL_NO = "roll_no";
	public static final String CONST_TANDC_ACCEPT = "tnc_accepted";
	public static final String CONST_ACC_DETAILS = "account_details";
	public static final String CONST_NOTES = "notes";
	public static final String NOTES1_MSG = "Penny Drop Test 1";
	public static final String NOTES2_MSG = "Penny Drop Test 2";
	public static final String CONST_ERROR = "error";
	public static final String CONST_DESCRIPTION = "description";
	public static final String CONST_ID = "id";
	public static final String CONTACT_NOTES1_MSG = "EKYC Contact Create";
	public static final String CONTACT_NOTES2_MSG = "EKYC Fund Account Contact";
	public static final String HTTP_AUTHORIZATION_KEYWORD = "authorization";
	public static final String HTTP_AUTH_BASIC_KEY = "Basic";
	public static final String HTTP_FAILED_MSG_KEY = "Failed : HTTP error code : ";
	public static final String CONST_ACCOUNT_TYPE = "account_type";
	public static final String CONST_ACCOUNT_NUMBER = "account_number";
	public static final String FUND_ACCOUNT_ID = "fund_account_id";
	public static final String FUND_ACCOUNT = "fund_account";
	public static final String MODE = "mode";
	public static final String IMPS = "IMPS";
	public static final String PURPOSE = "purpose";
	public static final String PAYOUT = "payout";
	public static final String LOW_BALANCE = "queue_if_low_balance";
	public static final String NARRATION = "narration";
	public static final String NARATION_MSG = "Penny Drop Test";
	// Rest Methods
	public static final String METHOD_NSDL = "NSDL";
	public static final String METHOD_CKYC = "CKYC";
	public static final String METHOD_DIGI = "DIGI_LOCKER";
	public static final String METHOD_CRE_PAY = "CREATE_PAYMENT";
	public static final String METHOD_VER_PAY = "VERIFY_PAYMENT";
	public static final String NOM_PROOF = "NOMINEE_PROOF";
	public static final String GUARDINA_PROOF = "GUARDIAN_PROOF";
	// KRA
	public static final String CONST_KRA_APP_RES_ROOT = "APP_RES_ROOT";
	public static final String CONST_KRA_APP_PAN_INQ = "APP_PAN_INQ";
	public static final String CONST_KRA_APP_NAME = "APP_NAME";
	public static final String CONST_KRA_ERROR = "ERROR";
	public static final String KRA_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	public static final String CONST_KYC_DATA = "KYC_DATA";
	public static final String CONST_KYC_ROOT = "ROOT";
	public static final int STATE_CODE = 1;
	public static final int COUNTRY_CODE = 2;

	// Req_Res_Table
	public static final String PAN = "PAN";
	public static final String DIGI = "DIGI";
	public static final String PAN_DOB = "PAN_DOB";
	public static final String CKYC = "CKYC";

	// ACCESS_REQ_RES_TBL
	public static final String SMS_VERIFY = "SMS_VERIFIED";
	public static final String EMAIL = "EMAIL";
	public static final String EMAIL_VERIFY = "EMAIL_VERIFY";
	public static final String BANK = "BANK";
	public static final String CREATE_PAYMENT = "CREATE_PAYMENT";
	public static final String CREATE_PAYOUT = "CREATE_PAYOUT";
	public static final String CREATE_CONTACT = "CREATE_CONTACT";
	public static final String ADD_ACOUNT = "ADD_ACOUNT";
	public static final String VERIFY_PAYMENT = "VERIFY_PAYMENT";
	public static final String DOC_UPLOAD = "UPLOAD_DOC";
	public static final String IVR_UPLOAD = "IVR_UPLOAD";
	public static final String NOMINEE = "NOMINEE";
	public static final String PROFILE = "PROFILE";
	public static final String SEGMENT = "SEGMENT";

	// BANK STATEMENT
	public static final String NO_NEED_BANK_STATEMENT = "BANK STATEMENT IS NOT  REQUIRED";
	public static final String NEED_BANK_STATEMENT = "LAST SIX MONTH BANK STATEMENT IS REQUIRED";

	// CKYC
	public static final String CONST_APPLICATION_JSON = "application/json";
	public static final String PAN_TYPE = "PAN";

	// Ivr
	public static final String IVR_FAILED_MESSAGE = "Failed to generate short URL";
	public static final String IVR_ACCEPT = "Accept";
	public static final String URL = "url";
	public static final String SHORT_URL = "shortLink";
	public static final String IVR_KEY = "&key=";
//	public static final String IVPBASEURL = "applicationId=";
	public static final String IVR_APPLICATIONID = "&applicationId=";
	public static final String IVR_NAME = "&name=";
	public static final String IVR_USER_DOMAIN_AND_RANDOMKEY = "&userDomain=1&randomKey=";
	public static final String IVR_SESSION = "&session=";
	public static final String IVR_MSG = "Dear user, your SKY eKYC Application IPV is pending. Kindly click here: ";

	public static final String STATEKEY = "1_STATE_";
	public static final String COUNTRYKEY = "2_COUNTRY_";
	public static final String INCOMEKEY = "3_INCOME_";
	public static final String OCCUPATIONKEY = "4_OCCUPATION_";
	public static final String PEPKEY = "5_PEP_";
	// Filter and Encryption
	public static final String CONST_FILTER = "FILTER";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String PATH_SEND_SMS_OTP = "/user/sendSmsOtp";
	public static final String PATH_TEST = "/user/testEsign";
	public static final String PATH_VERIFY_SMS_OTP = "/user/verifySmsOtp";
	public static final String PATH_DIGI_WH = "/digio/whDigilocker";
	public static final String PATH_RELOAD_KRAKEYVALUE = "/common/reloadKraKeyValue";
	public static final String PATH_GET_NSDL_ESIGN = "/pdf/getNsdlXml";
	public static final String PATH_GET_USR_DETAILS = "/user/getUsrDetails";
	public static final String PATH_LOG_TABLE = "/logs/Logtables";
	public static final String PATH_REST_LOG_TABLE = "/logs/RestServiceLogtables";
	
	
	public static final String CONST_REQ_BODY = "reqBody";
	public static final String CONST_IN_TIME = "inTime";
	public static final String EKYC_STATUS_PDF_GENERATED = "Pdf Generated";
	public static final String EKYC_STATUS_ESIGN_COMPLETED = "Esign Completed";
	
	
	// Mail Constants
	public static final String CONST_MAIL_HOST = "mail.smtp.host";
	public static final String CONST_MAIL_USER = "mail.smtp.user";
	public static final String CONST_MAIL_PORT = "mail.smtp.port";
	public static final String CONST_MAIL_SOC_FAC_PORT = "mail.smtp.socketFactory.port";
	public static final String CONST_MAIL_AUTH = "mail.smtp.auth";
	public static final String CONST_MAIL_DEBUG = "mail.smtp.debug";
	public static final String CONST_MAIL_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	public static final String CONST_MAIL_SSL_PROTOCOLS = "mail.smtp.ssl.protocols";
	public static final String CONST_MAIL_TLS_V2 = "TLSv1.2";
	/**
	 * 
	 */
	public static final String SITE_URL_FILE = "https://kyc.skybroking.com/completed";
	public static final String CONSTANT_TEXT_HTML = "text/html";
	
	public static final String ADDRESS_PROOF = "9_PROOF OF ADDRESS_";
	public static final String TABLE_CREATED = "Table Created Successfully";
}

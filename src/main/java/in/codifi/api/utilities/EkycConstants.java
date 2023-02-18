package in.codifi.api.utilities;

public class EkycConstants {
	public static final int FAILED_STATUS = 0;
	public static final int SUCCESS_STATUS = 1;
	public static final String FAILED_MSG = "Failed";
	public static final String SUCCESS_MSG = "Success";
	// SMS constants
	public static final String OTP_MSG = " is Your OTP for Registration with SKY COMMODITIES INDIA PVT. LTD";
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
	// OTP Validation
	public static final String SMS_KEY = "_SMS";
	public static final String EMAIL_KEY = "_EMAIL";
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
	public static final String PAGE_EMAIL = "1";
	public static final String PAGE_PAN = "2";
	public static final String PAGE_PAN_NSDL_DATA_CONFIRM = "2.1";
	public static final String PAGE_PAN_KRA_DOB_ENTRY = "2.2";
	public static final String PAGE_AADHAR = "3";
	public static final String PAGE_PROFILE = "4";
	public static final String PAGE_BANK = "5";
	public static final String PAGE_SEGMENT = "6";
	public static final String PAGE_PAYMENT = "7";
	public static final String PAGE_NOMINEE = "8";
	public static final String PAGE_NOMINEE_1 = "8.1";
	public static final String PAGE_NOMINEE_2 = "8.2";
	public static final String PAGE_NOMINEE_3 = "8.3";
	public static final String PAGE_IPV = "9";
	public static final String PAGE_DOCUMENT = "10";
	public static final String PAGE_PDFDOWNLOAD = "11";
	public static final String PAGE_ESIGN = "12";
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
	public static final String CONSTANT_URL_ENCODED = "application/x-www-form-urlencoded";
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
	public static final String DOC_IVR = "IVR";
	public static final String DOC_CHEQUE = "Cheque/Statement";
	public static final String DOC_INCOME = "Income";
	public static final String DOC_SIGNATURE = "Signature";
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
	// Rest Methods
	public static final String METHOD_NSDL = "NSDL";
	public static final String METHOD_CKYC = "CKYC";
	public static final String METHOD_DIGI = "DIGI_LOCKER";
	public static final String METHOD_CRE_PAY = "CREATE_PAYMENT";
	public static final String METHOD_VER_PAY = "VERIFY_PAYMENT";
	public static final String NOM_PROOF = "NOMINEE_PROOF";
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
	
	//Req_Res_Table
		public static final String PAN ="PAN";
		public static final String DIGI ="DIGI";
		public static final String PAN_DOB ="PAN_DOB";
		

		

}

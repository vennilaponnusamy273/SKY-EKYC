package in.codifi.api.config;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Singleton
@Getter
@Setter
public class ApplicationProperties {

	@ConfigProperty(name = "appconfig.sms.url")
	private String smsUrl;
	@ConfigProperty(name = "appconfig.sms.feedid")
	private String smsFeedId;
	@ConfigProperty(name = "appconfig.sms.senderid")
	private String smsSenderId;
	@ConfigProperty(name = "appconfig.sms.username")
	private String smsUserName;
	@ConfigProperty(name = "appconfig.sms.password")
	private String smsPassword;
	@ConfigProperty(name = "appconfig.mail.subject")
	private String mailSubject;
	@ConfigProperty(name = "appconfig.mail.text")
	private String mailText;
	@ConfigProperty(name = "appconfig.erp.token")
	private String erpToken;
	@ConfigProperty(name = "appconfig.pan.filepath")
	private String panFilePath;
	@ConfigProperty(name = "appconfig.pan.pfx.userid")
	private String panPfxUserId;
	@ConfigProperty(name = "appconfig.pan.pfx.password")
	private String panPfxPassword;
	@ConfigProperty(name = "appconfig.pan.pfx.filelocation")
	private String panPfxFileLocation;
	@ConfigProperty(name = "appconfig.pan.nsdlurl")
	private String panNsdlUrl;
	@ConfigProperty(name = "appconfig.pan.logsurl")
	private String panLogsUrl;
	@ConfigProperty(name = "appconfig.pan.version")
	private String panVersion;
	@ConfigProperty(name = "appconfig.digi.baseurl")
	private String digiBaseUrl;
	@ConfigProperty(name = "appconfig.digi.clientid")
	private String digiClientId;
	@ConfigProperty(name = "appconfig.digi.responsecode")
	private String digiResponseCode;
	@ConfigProperty(name = "appconfig.digi.secret")
	private String digiSecret;
	@ConfigProperty(name = "appconfig.digi.redirecturl")
	private String digiRedirectUrl;
	@ConfigProperty(name = "appconfig.digi.issueddocumenturl")
	private String digiIssuedDocUrl;
	@ConfigProperty(name = "appconfig.digi.filefromurl")
	private String digiFileFromUrl;
	@ConfigProperty(name = "appconfig.digi.aadharurl")
	private String digiAadharUrl;
	@ConfigProperty(name = "appconfig.razorpay.ifsc")
	private String razorpayIfscUrl;
	@ConfigProperty(name = "appconfig.razorpay.key")
	private String razorpayKey;
	@ConfigProperty(name = "appconfig.razorpay.secret")
	private String razorpaySecret;
	@ConfigProperty(name = "appconfig.file.basepath")
	private String fileBasePath;
	@ConfigProperty(name = "appconfig.ivr.ivrlive")
	private String ivrLiveUrl;
	@ConfigProperty(name = "appconfig.arya.token")
	private String aryaAiToken;
	@ConfigProperty(name = "appconfig.kra.username")
	private String kraUsername;
	@ConfigProperty(name = "appconfig.kra.poscode")
	private String kraPosCode;
	@ConfigProperty(name = "appconfig.kra.password")
	private String kraPassword;
	@ConfigProperty(name = "appconfig.kra.panstatus.url")
	private String kraPanStatusUrl;
	@ConfigProperty(name = "appconfig.kra.detailsfetch.url")
	private String kraDetailsFetchUrl;
	@ConfigProperty(name = "appconfig.check.erpnext")
	private boolean checkErpNext;
	@ConfigProperty(name = "appconfig.razorpay.create.contact")
	private String rzCreateContact;
	@ConfigProperty(name = "appconfig.razorpay.add.account")
	private String rzAddAccount;
	@ConfigProperty(name = "appconfig.razorpay.payout.account")
	private String rzPennyPayout;
	@ConfigProperty(name = "appconfig.razorpay.validate.account")
	private String rzValidateAccount;
	@ConfigProperty(name = "appconfig.razorpay.acc.number")
	private String rzAccountNumber;
	@ConfigProperty(name = "appconfig.ckyc.getdetails.url")
	private String ckycapi;
	// AliceBlue Msg Config
	@ConfigProperty(name = "appconfig.sms.mtalkz.url")
	private String url;
	@ConfigProperty(name = "appconfig.sms.mtalkz.apikey")
	private String apiKey;
	@ConfigProperty(name = "appconfig.sms.senderida")
	private String senderId;
	@ConfigProperty(name = "quarkus.bitly.access_token")
	private String bitlyAccessToken;
	@ConfigProperty(name = "quarkus.bitly_baseUrl")
	private String bitlyBaseUrl;
	@ConfigProperty(name = "appconfig.mail.ivr.text")
	private String bitText;
	@ConfigProperty(name = "quarkus.ivr_baseUrl")
	private String ivrBaseUrl;
}

package in.codifi.api.config;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Singleton
@Getter
@Setter
public class ApplicationProperties {
//	@ConfigProperty(name = "quarkus.erp.auth.token")
//	String UserCreationauthToken;
	@ConfigProperty(name = "appconfig.sms.url")
	String smsUrl;
	@ConfigProperty(name = "appconfig.sms.feedid")
	String smsFeedId;
	@ConfigProperty(name = "appconfig.sms.senderid")
	String smsSenderId;
	@ConfigProperty(name = "appconfig.sms.username")
	String smsUserName;
	@ConfigProperty(name = "appconfig.sms.text")
	String smsText;
	@ConfigProperty(name = "appconfig.sms.password")
	String smsPassword;
	@ConfigProperty(name = "appconfig.mail.subject")
	String mailSubject;
	@ConfigProperty(name = "appconfig.mail.text")
	String mailText;
	@ConfigProperty(name = "appconfig.erp.token")
	String erpToken;
	@ConfigProperty(name = "appconfig.pan.filepath")
	String panFilePath;
	@ConfigProperty(name = "appconfig.pan.pfx.userid")
	String panPfxUserId;
	@ConfigProperty(name = "appconfig.pan.pfx.password")
	String panPfxPassword;
	@ConfigProperty(name = "appconfig.pan.pfx.filelocation")
	String panPfxFileLocation;
	@ConfigProperty(name = "appconfig.pan.nsdlurl")
	String panNsdlUrl;
	@ConfigProperty(name = "appconfig.pan.logsurl")
	String panLogsUrl;
	@ConfigProperty(name = "appconfig.pan.version")
	String panVersion;
	@ConfigProperty(name = "appconfig.digi.baseurl")
	String digiBaseUrl;
	@ConfigProperty(name = "appconfig.digi.clientid")
	String digiClientId;
	@ConfigProperty(name = "appconfig.digi.responsecode")
	String digiResponseCode;
	@ConfigProperty(name = "appconfig.digi.secret")
	String digiSecret;
	@ConfigProperty(name = "appconfig.digi.redirecturl")
	String digiRedirectUrl;
	@ConfigProperty(name = "appconfig.digi.issueddocumenturl")
	String digiIssuedDocUrl;
	@ConfigProperty(name = "appconfig.digi.filefromurl")
	String digiFileFromUrl;
	@ConfigProperty(name = "appconfig.digi.aadharurl")
	String digiAadharUrl;
	@ConfigProperty(name = "appconfig.razorpay.ifsc")
	String razorpayIfscUrl;
	@ConfigProperty(name = "appconfig.razorpay.key")
	String razorpayKey;
	@ConfigProperty(name = "appconfig.razorpay.secret")
	String razorpaySecret;
	@ConfigProperty(name = "appconfig.file.basepath")
	String fileBasePath;
	@ConfigProperty(name = "appconfig.ivr.ivrlive")
	String ivrLiveUrl;
	@ConfigProperty(name = "appconfig.arya.token")
	String aryaAiToken;
	@ConfigProperty(name = "appconfig.ckyc.token")
	String ckycAiToken;
	@ConfigProperty(name = "appconfig.kra.username")
	String kraUsername;
	@ConfigProperty(name = "appconfig.kra.poscode")
	String kraPosCode;
	@ConfigProperty(name = "appconfig.kra.password")
	String kraPassword;
	@ConfigProperty(name = "appconfig.kra.panstatus.url")
	String kraPanStatusUrl;
	@ConfigProperty(name = "appconfig.kra.detailsfetch.url")
	String kraDetailsFetchUrl;
	@ConfigProperty(name = "appconfig.check.erpnext")
	boolean checkErpNext;
	@ConfigProperty(name = "appconfig.razorpay.create.contact")
	String rzCreateContact;
	@ConfigProperty(name = "appconfig.razorpay.add.account")
	String rzAddAccount;
	@ConfigProperty(name = "appconfig.razorpay.payout.account")
	String rzPennyPayout;
	@ConfigProperty(name = "appconfig.razorpay.validate.account")
	String rzValidateAccount;
	@ConfigProperty(name = "appconfig.razorpay.acc.number")
	String rzAccountNumber;
	@ConfigProperty(name = "appconfig.ckyc.getdetails.url")
	String ckycapi;
	// AliceBlue Msg Config
	@ConfigProperty(name = "appconfig.sms.mtalkz.url")
	String url;
	@ConfigProperty(name = "appconfig.sms.mtalkz.apikey")
	String apiKey;
	@ConfigProperty(name = "appconfig.sms.senderida")
	String senderId;
	@ConfigProperty(name = "appconfig.bitly.access.token")
	String bitlyAccessToken;
	@ConfigProperty(name = "appconfig.bitly.base.url")
	String bitlyBaseUrl;
	@ConfigProperty(name = "appconfig.mail.ivr.text")
	String bitText;
	@ConfigProperty(name = "appconfig.ipv.base.url")
	String ivrBaseUrl;	
	@ConfigProperty(name = "appconfig.address.url")
	String addressFetchUrl;
	// esign config
	@ConfigProperty(name = "appconfig.esign.pfx.userid")
	String esignUserId;
	@ConfigProperty(name = "appconfig.esign.pfx.password")
	String esignPassword;
	@ConfigProperty(name = "appconfig.esign.pfx.aspid")
	String esignAspId;
	@ConfigProperty(name = "appconfig.esign.pfx.alias")
	String esignAlias;
	@ConfigProperty(name = "appconfig.esign.pfx.location")
	String esignLocation;
	@ConfigProperty(name = "appconfig.esign.pfx.tickimage")
	String esignTickImage;
	@ConfigProperty(name = "appconfig.esign.doc.verifyimage")
	String verifyImage;
	@ConfigProperty(name = "quarkus.mailer.from")
	String mailfrom;
	@ConfigProperty(name = "appconfig.encrypt.key")
	String encryptkey;
	@ConfigProperty(name = "appconfig.encrypt.vector")
	String encryptinitVector;
	@ConfigProperty(name = "appconfig.token.encryption.key")
	String tokenEncryptKey;
	@ConfigProperty(name = "appconfig.esign.pdf")
	String PdfPath;
	@ConfigProperty(name = "appconfig.esign.pdf.fontfile")
	String pdfFontFile;

	@ConfigProperty(name = "appconfig.esign.return.url")
	String eSignReturnUrl;
	
	@ConfigProperty(name = "quarkus.mailer.host")
	String mailhost;
	@ConfigProperty(name = "quarkus.mailer.username")
	String MailUserName;
	@ConfigProperty(name = "quarkus.mailer.port")
	String MailPort;
	@ConfigProperty(name = "quarkus.mailer.password")
	String mailpassword;
	
	@ConfigProperty(name = "appconfig.esign.pdf.aadhar")
	String aadharPdfPath;
	@ConfigProperty(name = "appconfig.esign.pdf.pan")
	String panPdfPath;
	
	// digio
	@ConfigProperty(name = "appconfig.digio.baseurl")
	String digioBaseUrl;
	@ConfigProperty(name = "appconfig.digio.finalurl")
	String digioFinalUrl;
	@ConfigProperty(name = "appconfig.digio.auth.key")
	String digioAuthKey;
}

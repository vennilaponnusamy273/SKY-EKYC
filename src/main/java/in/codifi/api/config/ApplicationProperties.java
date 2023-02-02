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

}

package in.codifi.api.utilities;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.repository.EmailTemplateRepository;

@ApplicationScoped
public class CommonMail {
	@Inject
	ApplicationProperties props;
	@Inject
	EmailTemplateRepository emailTemplateRepository;
	
	public String sendMailWithFile(String mailIds, String name, String msg, String path) {
		EmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("Esign");
		StringBuilder builder = new StringBuilder();
		String success = EkycConstants.FAILED_MSG;
		try {
			Properties properties = new Properties();
			// Setup mail server
			properties.put(EkycConstants.CONST_MAIL_HOST, props.getMailhost());
			properties.put(EkycConstants.CONST_MAIL_USER, props.getMailUserName());
			properties.put(EkycConstants.CONST_MAIL_PORT, props.getMailPort());
			properties.put(EkycConstants.CONST_MAIL_SOC_FAC_PORT, props.getMailPort());
			properties.put(EkycConstants.CONST_MAIL_AUTH, EkycConstants.TRUE);
			properties.put(EkycConstants.CONST_MAIL_DEBUG, EkycConstants.TRUE);
			properties.put(EkycConstants.CONST_MAIL_STARTTLS_ENABLE, EkycConstants.TRUE);
			properties.put(EkycConstants.CONST_MAIL_SSL_PROTOCOLS, EkycConstants.CONST_MAIL_TLS_V2);
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(props.getMailfrom(), props.getMailpassword());
				}
			});
			try {
				String body_Message = emailTempentity.getBody();
				String body = body_Message.replace("{UserName}", name);
				builder.append(body);
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(props.getMailfrom()));
				message.addRecipients(Message.RecipientType.TO, mailIds);
				message.setSubject(emailTempentity.getSubject());
				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setContent(builder.toString(), EkycConstants.CONSTANT_TEXT_HTML);
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart1);

				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				attachmentBodyPart.attachFile(path);
				multipart.addBodyPart(attachmentBodyPart);
				message.setContent(multipart);
				Transport.send(message);
				success = EkycConstants.SUCCESS_MSG;
			} catch (MessagingException ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;

	}
}

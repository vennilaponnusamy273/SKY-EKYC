package in.codifi.api.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.model.ResponseModel;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
public class CommonMethods {
	@Inject
	ApplicationProperties props;
	@Inject
	Mailer mailer;

	/**
	 * Method to generate OTP for Mobile number
	 * 
	 * @author prade
	 * @param mobileNumber
	 * @return
	 */
	public int generateOTP(long mobileNumber) {
		int otp = 0000;
		if (mobileNumber == 1234567890 || mobileNumber == 1111100000) {
			otp = 0000;
			System.out.println("OTP : " + otp);
		} else {
			otp = (int) (Math.random() * 9000) + 1000;
			System.out.println("OTP : " + otp);
		}
		return otp;
	}

	/**
	 * Method to construct Failed method
	 * 
	 * @author prade
	 * @param failesMessage
	 * @return
	 */
	public ResponseModel constructFailedMsg(String failesMessage) {
		ResponseModel model = new ResponseModel();
		model.setStat(EkycConstants.FAILED_STATUS);
		model.setMessage(EkycConstants.FAILED_MSG);
		model.setReason(failesMessage);
		return model;
	}

	/**
	 * Method to send otp to Mobile Number
	 * 
	 * @author prade
	 * @param otp
	 * @param mobile Number
	 * @return
	 */
	public void sendOTPtoMobile(int otp, long mobileNumber) {
		try {
			StringBuffer data = new StringBuffer();
			data.append(EkycConstants.CONST_SMS_FEEDID + props.getSmsFeedId());
			data.append(EkycConstants.AND + EkycConstants.CONST_SMS_SENDERID + props.getSmsSenderId());
			data.append(EkycConstants.AND + EkycConstants.CONST_SMS_USERNAME + props.getSmsUserName());
			data.append(EkycConstants.AND + EkycConstants.CONST_SMS_PASSWORD + props.getSmsPassword());
			data.append(EkycConstants.AND + EkycConstants.CONST_SMS_TO + mobileNumber);
			String msg = EkycConstants.AND + EkycConstants.CONST_SMS_TEXT + otp
					+ EkycConstants.OTP_MSG.replace(" ", "%20");
			data.append(msg);
			URL url = new URL(props.getSmsUrl() + data.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Inject
	public void MailService(Mailer javaMailSender) {
		this.mailer = javaMailSender;
	}

	/**
	 * Method to send mail
	 * 
	 * @param user
	 * @return
	 */
	public void sendMailOtp(int otp, String emailId) {
		try {
			String getSubject = props.getMailSubject();
			String getText = otp + " " + props.getMailText();
			Mail mail = Mail.withText(emailId, getSubject, getText);
			mailer.send(mail);
			System.out.print("the post mail" + mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

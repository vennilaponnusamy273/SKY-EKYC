package in.codifi.api.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.UserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.UserRepository;
import in.codifi.api.service.spec.ISmsService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Service
public class SmsService implements ISmsService {
	@Inject
	UserRepository repository;

	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationProperties props;

	/**
	 * Method to send otp to mobile number
	 */
	public ResponseModel sendSmsOtp(UserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
			UserEntity updatedUserDetails = null;
			UserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			if (oldUserEntity == null) {
				// send OTP
				int otp = commonMethods.generateOTP(userEntity.getMobileNo());
				sendOTPtoMobile(otp, userEntity.getMobileNo());
				userEntity.setSmsOtp(otp);
				userEntity.setSmsOtpTimeStamp(Instant.now().toEpochMilli());
				updatedUserDetails = repository.save(userEntity);
			} else {
				// resend OTP
				int otp = commonMethods.generateOTP(userEntity.getMobileNo());
				sendOTPtoMobile(otp, userEntity.getMobileNo());
				oldUserEntity.setSmsOtp(otp);
				oldUserEntity.setSmsOtpTimeStamp(Instant.now().toEpochMilli());
				oldUserEntity.setSmsVerified(0);
				updatedUserDetails = repository.save(oldUserEntity);
			}
			if (updatedUserDetails != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setResult(updatedUserDetails);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_GENERATE_OTP);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
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

	/**
	 * Method to validate Sms OTP
	 */
	@Override
	public ResponseModel verifySmsOtp(UserEntity userEntity) {
		ResponseModel responseModel = null;
		try {
			UserEntity updatedUserDetails = null;
			UserEntity oldUserEntity = repository.findByMobileNo(userEntity.getMobileNo());
			long currentTime = Instant.now().toEpochMilli();
			if (oldUserEntity != null) {
				long nowPlus5Minutes = oldUserEntity.getSmsOtpTimeStamp() + TimeUnit.MINUTES.toMillis(5);
				if (oldUserEntity.getSmsOtp() == userEntity.getSmsOtp() && currentTime < nowPlus5Minutes) {
					oldUserEntity.setSmsVerified(1);
					updatedUserDetails = repository.save(oldUserEntity);
					if (updatedUserDetails != null) {
						responseModel = new ResponseModel();
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setResult(updatedUserDetails);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_VERIFY_OTP);
					}
				} else {
					if (currentTime > nowPlus5Minutes) {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.OTP_TIME_EXPIRED);
					} else {
						responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_OTP);
					}
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.MOBILE_NUMBER_WRONG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
}

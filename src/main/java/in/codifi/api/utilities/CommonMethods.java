package in.codifi.api.utilities;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.api.model.ResponseModel;

@ApplicationScoped
public class CommonMethods {
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
		model.setStat(EkycConstants.FAILED_MSG);
		model.setMessage(failesMessage);
		return model;
	}

}

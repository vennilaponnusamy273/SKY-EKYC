package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RzVerifyPaymentModel {
	@JsonProperty("razorpay_payment_id")
	private String razorpayPaymentId;
	@JsonProperty("razorpay_order_id")
	private String razorpayOrderId;
	@JsonProperty("razorpay_signature")
	private String razorpaySignature;
}

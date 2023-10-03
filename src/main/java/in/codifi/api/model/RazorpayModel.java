package in.codifi.api.model;

import com.razorpay.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RazorpayModel {
	private int stat;
	private String message;
	private String reason;
	private Order order;
}

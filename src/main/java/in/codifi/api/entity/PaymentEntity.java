package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_payment_details")
@Getter
@Setter
public class PaymentEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "amount_paid")
	private int amountPaid;

	@Column(name = "amount_due")
	private int amountDue;

	@Column(name = "amount", nullable = false)
	private int amount;

	@Column(name = "currency")
	private String currency;

	@Column(name = "receipt")
	private String receipt;

	@Column(name = "razorpay_order_id")
	private String razorpayOrderId;

	@Column(name = "razorpay_payment_id")
	private String razorpayPaymentId;

	@Column(name = "Razorpay_signature")
	private String razorpaySignature;

	@Column(name = "attempts")
	private int attempts;

	@Column(name = "payment_id")
	private String paymentId;

	@Column(name = "entity")
	private String entity;

	@Column(name = "status")
	private String status;

	@Column(name = "referrence_id")
	private String referrenceId;

	@Column(name = "notes")
	private String notes;

	@Column(name = "order_id")
	private int orderId;

	@Column(name = "verify_url")
	private String verifyUrl;
}

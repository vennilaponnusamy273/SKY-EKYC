package in.codifi.api.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.model.RazorpayModel;
import in.codifi.api.model.RzVerifyPaymentModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.PaymentRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Service
public class PaymentHelper {
	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;
	@Inject
	PaymentRepository paymentRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	ApplicationUserRepository userRepository;

	public List<String> validateCreatePayment(PaymentEntity paymentEntity) {
		List<String> errorMsg = new ArrayList<>();
		if (paymentEntity != null) {
			PaymentEntity entity = paymentRepository.findByApplicationId(paymentEntity.getApplicationId());
			if (entity != null) {
				errorMsg.add(MessageConstants.PAYMENT_ALREADY_CREATED);
			} else {
				if (paymentEntity.getApplicationId() <= 0) {
					errorMsg.add(MessageConstants.USER_ID_NULL);
				}
				if (paymentEntity.getAmount() <= 0) {
					errorMsg.add(MessageConstants.AMOUNT_NULL);
				}
			}
		} else {
			errorMsg.add(MessageConstants.PARAMETER_NULL);
		}
		return errorMsg;
	}

	public List<String> validateVerifyPayment(PaymentEntity paymentEntity) {
		List<String> errorMsg = new ArrayList<>();
		if (paymentEntity != null) {
			if (paymentEntity.getApplicationId() == null || paymentEntity.getApplicationId() <= 0) {
				errorMsg.add(MessageConstants.USER_ID_NULL);
			}
			if (StringUtil.isNullOrEmpty(paymentEntity.getVerifyUrl())) {
				errorMsg.add(MessageConstants.VERIFY_URL_NULL);
			}
		} else {
			errorMsg.add(MessageConstants.PARAMETER_NULL);
		}
		return errorMsg;
	}

	/**
	 * Method to create payment in Razor pay
	 * 
	 * @param paymentEntity
	 * @return
	 */
	public RazorpayModel createPayment(PaymentEntity paymentEntity) {
		RazorpayModel responseDTO = new RazorpayModel();
		try {
			Order order = null;
			BankEntity bankEntity = bankRepository.findByapplicationId(paymentEntity.getApplicationId());
			Optional<ApplicationUserEntity> userEntity = userRepository.findById(paymentEntity.getApplicationId());
			if (bankEntity != null) {
				JSONObject bankJson = new JSONObject();
				if (StringUtil.isNotNullOrEmpty(bankEntity.getAccountNo())) {
					bankJson.put(EkycConstants.CONST_BANK_ACCOUNT_NUMBER, bankEntity.getAccountNo());
				}
				if (StringUtil.isNotNullOrEmpty(userEntity.get().getUserName())) {
					bankJson.put(EkycConstants.CONST_BANK_NAME, userEntity.get().getUserName());
				}
				if (StringUtil.isNotNullOrEmpty(bankEntity.getIfsc())) {
					bankJson.put(EkycConstants.CONST_BANK_IFSC, bankEntity.getIfsc());
				}
				CommonMethods.trustedManagement();
				RazorpayClient razorpay = new RazorpayClient(props.getRazorpayKey(), props.getRazorpaySecret());
				JSONObject orderRequest = new JSONObject();
				orderRequest.put(EkycConstants.AMOUNT, paymentEntity.getAmount() * 100);
				orderRequest.put(EkycConstants.CURRENCY, EkycConstants.RAZORPAY_CURRENCY_INR);
				orderRequest.put(EkycConstants.RECEIPT, String.valueOf(paymentEntity.getApplicationId()));
				orderRequest.put(EkycConstants.CONST_BANK_ACCOUNT, bankJson);
				order = razorpay.orders.create(orderRequest);
				responseDTO.setStat(EkycConstants.SUCCESS_STATUS);
				responseDTO.setOrder(order);
				commonMethods.saveRequestAndResposne(orderRequest.toString(), order.toString(),
						EkycConstants.METHOD_CRE_PAY, paymentEntity.getApplicationId());
			} else {
				responseDTO.setStat(EkycConstants.FAILED_STATUS);
				responseDTO.setMessage(MessageConstants.BANK_DETAILS_NULL);
			}
		} catch (Exception e) {
			responseDTO.setStat(EkycConstants.FAILED_STATUS);
			responseDTO.setMessage(e.toString());
			e.printStackTrace();
		}
		return responseDTO;
	}

	/**
	 * Method to insert PaymentDto
	 * 
	 * @author Pradeep Ravichandran
	 * @param order
	 * @param dto
	 * @return
	 */
	public PaymentEntity populateRequiredFeilds(Order order, PaymentEntity paymentDto) {
		PaymentEntity dto = new PaymentEntity();
		dto.setPaymentId(order.get("id"));
		int amount = order.get("amount");
		dto.setAmount(amount / 100);
		dto.setCurrency(order.get("currency"));
		dto.setReceipt(order.get("receipt"));
		dto.setAmountPaid(order.get("amount_paid"));
		int amountDue = order.get("amount_due");
		dto.setAmountDue(amountDue / 100);
		dto.setEntity(order.get("entity"));
		dto.setStatus(order.get("status"));
		dto.setAttempts(order.get("attempts"));
		dto.setApplicationId(paymentDto.getApplicationId());
		PaymentEntity savePaymentEntity = paymentRepository.save(dto);
		return savePaymentEntity;
	}

	/**
	 * Method to save verify Payment
	 * 
	 * @author Pradeep Ravichandran
	 * @param order
	 * @param dto
	 * @return
	 */
//	public PaymentEntity saveVerifyPayment(PaymentEntity paymentDto) {
//		PaymentEntity dto = paymentRepository.findByApplicationId(paymentDto.getApplicationId());
//		dto.setRazorpayOrderId(paymentDto.getRazorpayOrderId());
//		dto.setRazorpayPaymentId(paymentDto.getRazorpayPaymentId());
//		dto.setRazorpaySignature(paymentDto.getRazorpaySignature());
//		dto.setStatus(EkycConstants.RAZORPAY_STATUS_COMPLETED);
//		dto.setAmountPaid(paymentDto.getAmount());
//		dto.setVerifyUrl(paymentDto.getVerifyUrl());
//		dto.setAmountDue(dto.getAmountDue() - paymentDto.getAmount());
//		PaymentEntity savePaymentEntity = paymentRepository.save(dto);
//		return savePaymentEntity;
//	}

	/**
	 * method to verify Payment in razerpay
	 * 
	 * @author Pradeep Ravichandran
	 * @param dto
	 * @return
	 */
	public PaymentEntity verifyPayment(PaymentEntity dto) {
		PaymentEntity savePaymentEntity = null;
		try {
			String result = getPaymentResult(dto.getVerifyUrl());
			ObjectMapper mapper = new ObjectMapper();
			RzVerifyPaymentModel paymentModel = mapper.readValue(result, RzVerifyPaymentModel.class);
			JSONObject orderRequest = new JSONObject();
			orderRequest.put(EkycConstants.AMOUNT, dto.getAmount());
			orderRequest.put(EkycConstants.CURRENCY, EkycConstants.RAZORPAY_CURRENCY_INR);
			orderRequest.put(EkycConstants.RECEIPT, dto.getReceipt());
			orderRequest.put(EkycConstants.RAZORPAY_ORDERID, paymentModel.getRazorpayOrderId());
			orderRequest.put(EkycConstants.RAZORPAY_PAYMENTID, paymentModel.getRazorpayPaymentId());
			orderRequest.put(EkycConstants.RAZORPAY_SIGNATURE, paymentModel.getRazorpaySignature());
			boolean isEqual = Utils.verifyPaymentSignature(orderRequest, props.getRazorpaySecret());
			if (isEqual) {
				dto.setRazorpayOrderId(paymentModel.getRazorpayOrderId());
				dto.setRazorpayPaymentId(paymentModel.getRazorpayPaymentId());
				dto.setRazorpaySignature(paymentModel.getRazorpaySignature());
				dto.setStatus(EkycConstants.RAZORPAY_STATUS_COMPLETED);
				dto.setAmountPaid(dto.getAmount());
				dto.setAmountDue(dto.getAmountDue() - dto.getAmount());
				savePaymentEntity = paymentRepository.save(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savePaymentEntity;
	}

	/**
	 * Method to reset Poa Status
	 * 
	 * @author Mithun CR
	 * @return
	 */
	public String getPaymentResult(String verifyUrl) {
		String result = "";
		HttpURLConnection conn = null;
		try {
			URL url = new URL(verifyUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br1.readLine()) != null) {
				result = output;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

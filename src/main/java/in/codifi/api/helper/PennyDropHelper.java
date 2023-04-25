package in.codifi.api.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.PennyDropEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.PennyDropRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class PennyDropHelper {
	@Inject
	ApplicationProperties props;
	@Inject
	PennyDropRepository pennyDropRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	CommonMethods commonMethods;
	private static final Logger logger = LogManager.getLogger(PennyDropHelper.class);
	/**
	 * Method to Create Contact in razorpay
	 * 
	 * @author prade
	 * @param applicationUserEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResponseModel createContact(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseDTO = new ResponseModel();
		PennyDropEntity pennyDropDTO = null;
		try {
			PennyDropEntity oldDataEntity = pennyDropRepository.findByapplicationId(applicationUserEntity.getId());
			JSONObject createContactJSON = new JSONObject();
			JSONObject notes = new JSONObject();
			createContactJSON.put(EkycConstants.CONST_NAME, applicationUserEntity.getUserName());
			createContactJSON.put(EkycConstants.CONST_EMAIL, applicationUserEntity.getEmailId());
			createContactJSON.put(EkycConstants.CONST_CONTACT, Long.toString(applicationUserEntity.getMobileNo()));
			createContactJSON.put(EkycConstants.CONST_TYPE, EkycConstants.CONST_CUSTOMER);
			createContactJSON.put(EkycConstants.CONST_REFERENCE_ID, applicationUserEntity.getId().toString());
			notes.put(EkycConstants.CONST_NOTES_1, EkycConstants.CONTACT_NOTES1_MSG);
			notes.put(EkycConstants.CONST_NOTES_2, EkycConstants.CONTACT_NOTES2_MSG);
			createContactJSON.put(EkycConstants.CONST_NOTES, notes);
			URL url = new URL(props.getRzCreateContact());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.HTTP_AUTHORIZATION_KEYWORD,
					EkycConstants.HTTP_AUTH_BASIC_KEY + " " + getRzAuthStringEnc());
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = createContactJSON.toJSONString().getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			} catch (Exception e) {
				logger.error("An error occurred: " + e.getMessage());
				commonMethods.SaveLog(applicationUserEntity.getId(),"PennyDropHelper","createContact",e.getMessage());
				commonMethods.sendErrorMail("An error occurred while processing your request, In createContact.","ERR-001");
				e.printStackTrace();
			}
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
				String f = in.readLine();
				in.close();
				JSONParser parser = new JSONParser();
				JSONObject response1 = (JSONObject) parser.parse(f);
				if ((JSONObject) response1.get(EkycConstants.CONST_ERROR) != null) {
					JSONObject errorJson = (JSONObject) response1.get(EkycConstants.CONST_ERROR);
					System.out.print(errorJson.get(EkycConstants.CONST_DESCRIPTION));
					responseDTO.setResult(errorJson.get(EkycConstants.CONST_DESCRIPTION));
				}
				responseDTO.setStat(EkycConstants.FAILED_STATUS);
				responseDTO.setMessage(EkycConstants.FAILED_MSG);
			} else {
				BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br1.readLine()) != null) {
					JSONObject object1 = (JSONObject) JSONValue.parse(output);
					if (oldDataEntity != null) {
						pennyDropDTO = oldDataEntity;
					} else {
						pennyDropDTO = new PennyDropEntity();
					}
					pennyDropDTO.setApplicationId(applicationUserEntity.getId());
					ObjectMapper mapper = new ObjectMapper();
					pennyDropDTO.setRzReqContactJson(mapper.writeValueAsString(createContactJSON));
					if (StringUtil.isNotNullOrEmpty(object1.get(EkycConstants.CONST_ID).toString())) {
						pennyDropDTO.setRzContactId(object1.get(EkycConstants.CONST_ID).toString());
					}
					pennyDropDTO.setRzResContactJson(output);
					pennyDropDTO.setPan(applicationUserEntity.getPanNumber());
					pennyDropDTO.setEmail(applicationUserEntity.getEmailId());
					pennyDropDTO.setMobileNumber(Long.toString(applicationUserEntity.getMobileNo()));
					PennyDropEntity savedPennyDrop = pennyDropRepository.save(pennyDropDTO);
					if (savedPennyDrop != null && savedPennyDrop.getId() > 0) {
						responseDTO.setStat(EkycConstants.SUCCESS_STATUS);
						responseDTO.setMessage(EkycConstants.SUCCESS_MSG);
						responseDTO.setResult(object1);
					} else {
						responseDTO.setStat(EkycConstants.FAILED_STATUS);
						responseDTO.setMessage(EkycConstants.FAILED_MSG);
						responseDTO.setReason("Table Not Updated");
					}
				}
				br1.close();
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationUserEntity.getId(),"PennyDropHelper","createContact",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In createContact.","ERR-001");
			responseDTO.setStat(EkycConstants.FAILED_STATUS);
			responseDTO.setMessage(EkycConstants.FAILED_MSG);
		}
		return responseDTO;
	}

	/**
	 * Method to add Account in created Contact
	 * 
	 * @author prade
	 * @param applicationUserEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResponseModel addAccount(ApplicationUserEntity applicationUserEntity, PennyDropEntity pennyDropEntity,
			BankEntity bankEntity) {
		ResponseModel responseDTO = new ResponseModel();
		try {
			JSONObject addAccountJSON = new JSONObject();
			JSONObject bankAccount = new JSONObject();
			addAccountJSON.put(EkycConstants.CONST_CONATCT_ID, pennyDropEntity.getRzContactId());
			addAccountJSON.put(EkycConstants.CONST_ACCOUNT_TYPE, EkycConstants.CONST_BANK_ACCOUNT);
			bankAccount.put(EkycConstants.CONST_NAME, applicationUserEntity.getUserName());
			bankAccount.put(EkycConstants.CONST_IFSC, bankEntity.getIfsc());
			bankAccount.put(EkycConstants.CONST_ACCOUNT_NUMBER, bankEntity.getAccountNo());
			addAccountJSON.put(EkycConstants.CONST_BANK_ACCOUNT, bankAccount);
			URL url = new URL(props.getRzAddAccount());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.HTTP_AUTHORIZATION_KEYWORD,
					EkycConstants.HTTP_AUTH_BASIC_KEY + " " + getRzAuthStringEnc());
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = addAccountJSON.toJSONString().getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			} catch (Exception e) {
				logger.error("An error occurred: " + e.getMessage());
				commonMethods.SaveLog(applicationUserEntity.getId(),"PennyDropHelper","addAccount",e.getMessage());
				commonMethods.sendErrorMail("An error occurred while processing your request, In addAccount.","ERR-001");
				e.printStackTrace();
			}
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
				String f = in.readLine();
				in.close();
				JSONParser parser = new JSONParser();
				JSONObject response1 = (JSONObject) parser.parse(f);
				if ((JSONObject) response1.get(EkycConstants.CONST_ERROR) != null) {
					JSONObject errorJson = (JSONObject) response1.get(EkycConstants.CONST_ERROR);
					System.out.print(errorJson.get(EkycConstants.CONST_DESCRIPTION));
					responseDTO.setResult(errorJson.get(EkycConstants.CONST_DESCRIPTION));
				}
				responseDTO.setStat(EkycConstants.FAILED_STATUS);
				responseDTO.setMessage(EkycConstants.FAILED_MSG);
			} else {
				BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br1.readLine()) != null) {
					JSONObject object1 = (JSONObject) JSONValue.parse(output);
					if (StringUtil.isNotNullOrEmpty(object1.get(EkycConstants.CONST_ID).toString())) {
						pennyDropEntity.setRzFundAccountId(object1.get(EkycConstants.CONST_ID).toString());
					}
					pennyDropEntity.setRzResFundJson(output);
					ObjectMapper mapper = new ObjectMapper();
					pennyDropEntity.setAccNumber(bankEntity.getAccountNo());
					pennyDropEntity.setIfsc(bankEntity.getIfsc());
					pennyDropEntity.setRzReqFundJson(mapper.writeValueAsString(addAccountJSON));
					PennyDropEntity savedPennyDrop = pennyDropRepository.save(pennyDropEntity);
					if (savedPennyDrop != null && StringUtil.isNotNullOrEmpty(savedPennyDrop.getRzFundAccountId())) {
						responseDTO.setStat(EkycConstants.SUCCESS_STATUS);
						responseDTO.setMessage(EkycConstants.SUCCESS_MSG);
						responseDTO.setResult(savedPennyDrop);
					} else {
						responseDTO.setStat(EkycConstants.FAILED_STATUS);
						responseDTO.setMessage(EkycConstants.FAILED_MSG);
						responseDTO.setReason("Table Not Updated");
					}
				}
				br1.close();
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationUserEntity.getId(),"PennyDropHelper","addAccount",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In addAccount.","ERR-001");
			responseDTO.setStat(EkycConstants.FAILED_STATUS);
			responseDTO.setMessage(EkycConstants.FAILED_MSG);
		}
		return responseDTO;
	}

	/**
	 * Method to put some penny Amount
	 * 
	 * @param applicationUserEntity
	 * @param pennyDropEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResponseModel createPayout(ApplicationUserEntity applicationUserEntity, PennyDropEntity pennyDropEntity) {
		ResponseModel responseDTO = new ResponseModel();
		try {
			int amount = 100;
			JSONObject payOutJSON = new JSONObject();
			JSONObject notesJSON = new JSONObject();
			payOutJSON.put(EkycConstants.CONST_ACCOUNT_NUMBER, props.getRzAccountNumber());
			payOutJSON.put(EkycConstants.FUND_ACCOUNT_ID, pennyDropEntity.getRzFundAccountId());
			payOutJSON.put(EkycConstants.AMOUNT, amount);
			payOutJSON.put(EkycConstants.CURRENCY, EkycConstants.RAZORPAY_CURRENCY_INR);
			payOutJSON.put(EkycConstants.MODE, EkycConstants.IMPS);
			payOutJSON.put(EkycConstants.PURPOSE, EkycConstants.PAYOUT);
			payOutJSON.put(EkycConstants.LOW_BALANCE, true);
			payOutJSON.put(EkycConstants.CONST_REFERENCE_ID, pennyDropEntity.getApplicationId().toString());
			payOutJSON.put(EkycConstants.NARRATION, EkycConstants.NARATION_MSG);
			notesJSON.put(EkycConstants.CONST_NOTES_1, EkycConstants.NOTES1_MSG);
			notesJSON.put(EkycConstants.CONST_NOTES_2, EkycConstants.NOTES2_MSG);
			payOutJSON.put(EkycConstants.CONST_NOTES, notesJSON);
			URL url = new URL(props.getRzPennyPayout());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.HTTP_AUTHORIZATION_KEYWORD,
					EkycConstants.HTTP_AUTH_BASIC_KEY + " " + getRzAuthStringEnc());
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = payOutJSON.toJSONString().getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			} catch (Exception e) {
				logger.error("An error occurred: " + e.getMessage());
				commonMethods.SaveLog(applicationUserEntity.getId(),"PennyDropHelper","createPayout",e.getMessage());
				commonMethods.sendErrorMail("An error occurred while processing your request, In createPayout.","ERR-001");
				e.printStackTrace();
			}
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
				String f = in.readLine();
				in.close();
				JSONParser parser = new JSONParser();
				JSONObject response1 = (JSONObject) parser.parse(f);
				if ((JSONObject) response1.get(EkycConstants.CONST_ERROR) != null) {
					JSONObject errorJson = (JSONObject) response1.get(EkycConstants.CONST_ERROR);
					System.out.print(errorJson.get(EkycConstants.CONST_DESCRIPTION));
					responseDTO.setResult(errorJson.get(EkycConstants.CONST_DESCRIPTION));
				}
				responseDTO.setStat(EkycConstants.FAILED_STATUS);
				responseDTO.setMessage(EkycConstants.FAILED_MSG);
			} else {
				BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br1.readLine()) != null) {
					JSONObject object1 = (JSONObject) JSONValue.parse(output);
					if (StringUtil.isNotNullOrEmpty(object1.get(EkycConstants.CONST_ID).toString())) {
						pennyDropEntity.setRzPayoutId(object1.get(EkycConstants.CONST_ID).toString());
					}
					pennyDropEntity.setRzResPayoutJson(output);
					pennyDropEntity.setPennyAmount(amount);
					ObjectMapper mapper = new ObjectMapper();
					pennyDropEntity.setRzReqPayoutJson(mapper.writeValueAsString(payOutJSON));
					pennyDropEntity.setConfirmPenny(1);
					PennyDropEntity savedPennyDrop = pennyDropRepository.save(pennyDropEntity);
					if (savedPennyDrop != null && StringUtil.isNotNullOrEmpty(savedPennyDrop.getRzFundAccountId())) {
						responseDTO.setStat(EkycConstants.SUCCESS_STATUS);
						responseDTO.setMessage(EkycConstants.SUCCESS_MSG);
						commonMethods.UpdateStep(EkycConstants.PAGE_PENNY, applicationUserEntity.getId());
						responseDTO.setResult(savedPennyDrop);
					} else {
						responseDTO.setStat(EkycConstants.FAILED_STATUS);
						responseDTO.setMessage(EkycConstants.FAILED_MSG);
						responseDTO.setReason("Table Not Updated");
					}
				}
				br1.close();
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(applicationUserEntity.getId(),"PennyDropHelper","createPayout",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In createPayout.","ERR-001");
			responseDTO.setStat(EkycConstants.FAILED_STATUS);
			responseDTO.setMessage(EkycConstants.FAILED_MSG);
		}
		return responseDTO;
	}

	/**
	 * Method to Validate Penny Details
	 * 
	 * @author prade
	 * @param pennyDropEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResponseModel ValidateDetails(PennyDropEntity pennyDropEntity) {
		ResponseModel responseDTO = new ResponseModel();
		try {
			int amount = 100;
			JSONObject fundAccountJSON = new JSONObject();
			JSONObject notesJSON = new JSONObject();
			JSONObject validateJSON = new JSONObject();
			validateJSON.put(EkycConstants.CONST_ID, pennyDropEntity.getRzFundAccountId());
			fundAccountJSON.put(EkycConstants.CONST_ACCOUNT_NUMBER, props.getRzAccountNumber());
			fundAccountJSON.put(EkycConstants.AMOUNT, amount);
			fundAccountJSON.put(EkycConstants.FUND_ACCOUNT, validateJSON);
			notesJSON.put(EkycConstants.CONST_NOTES_1, EkycConstants.NOTES1_MSG);
			notesJSON.put(EkycConstants.CONST_NOTES_2, EkycConstants.NOTES2_MSG);
			fundAccountJSON.put(EkycConstants.CONST_NOTES, notesJSON);
			URL url = new URL(props.getRzValidateAccount());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_POST);
			conn.setRequestProperty(EkycConstants.HTTP_AUTHORIZATION_KEYWORD,
					EkycConstants.HTTP_AUTH_BASIC_KEY + " " + getRzAuthStringEnc());
			conn.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_APPLICATION_JSON);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = fundAccountJSON.toJSONString().getBytes(EkycConstants.CONSTANT_URL_UF8);
				os.write(input, 0, input.length);
			} catch (Exception e) {
				logger.error("An error occurred: " + e.getMessage());
				commonMethods.SaveLog(pennyDropEntity.getApplicationId(),"PennyDropHelper","ValidateDetails",e.getMessage());
				commonMethods.sendErrorMail("An error occurred while processing your request, In ValidateDetails.","ERR-001");
				e.printStackTrace();
			}
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
				String f = in.readLine();
				in.close();
				JSONParser parser = new JSONParser();
				JSONObject response1 = (JSONObject) parser.parse(f);
				if ((JSONObject) response1.get(EkycConstants.CONST_ERROR) != null) {
					JSONObject errorJson = (JSONObject) response1.get(EkycConstants.CONST_ERROR);
					System.out.print(errorJson.get(EkycConstants.CONST_DESCRIPTION));
					responseDTO.setResult(errorJson.get(EkycConstants.CONST_DESCRIPTION));
				}
				responseDTO.setStat(EkycConstants.FAILED_STATUS);
				responseDTO.setMessage(EkycConstants.FAILED_MSG);
			} else {
				BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br1.readLine()) != null) {
					System.out.println("===========> BANK NAME" + output);
					JSONObject object1 = (JSONObject) JSONValue.parse(output);
					JSONObject one = (JSONObject) object1.get("results");
					if (one != null && one.containsKey("registered_name")) {
						Object registeredName = one.get("registered_name");
						if (registeredName != null) {
							pennyDropEntity.setAccountHolderName(one.get("registered_name").toString());
						}
					}
					pennyDropEntity.setRzResValidationJson(output);
					ObjectMapper mapper = new ObjectMapper();
					pennyDropEntity.setRzReqValidationJson(mapper.writeValueAsString(fundAccountJSON));
					PennyDropEntity savedPennyDrop = pennyDropRepository.save(pennyDropEntity);
					if (savedPennyDrop != null && StringUtil.isNotNullOrEmpty(savedPennyDrop.getRzFundAccountId())) {
						responseDTO.setStat(EkycConstants.SUCCESS_STATUS);
						responseDTO.setMessage(EkycConstants.SUCCESS_MSG);
						responseDTO.setResult(savedPennyDrop);
					} else {
						responseDTO.setStat(EkycConstants.FAILED_STATUS);
						responseDTO.setMessage(EkycConstants.FAILED_MSG);
						responseDTO.setReason("Table Not Updated");
					}
				}
				br1.close();
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(pennyDropEntity.getApplicationId(),"PennyDropHelper","ValidateDetails",e.getMessage());
			commonMethods.sendErrorMail("An error occurred while processing your request, In ValidateDetails.","ERR-001");
			responseDTO = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseDTO;
	}

	public String getRzAuthStringEnc() {
		String Key = props.getRazorpayKey();
		String secret = props.getRazorpaySecret();
		String authString = Key + ":" + secret;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		return new String(authEncBytes);
	}

}

package in.codifi.api.controller;

import java.time.LocalTime;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.controller.spec.IDigioController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.model.WebhookDigilockerRequestModel;
import in.codifi.api.model.WebhookDigioPayloadModel;
import in.codifi.api.model.WebhookDigioRequestModel;
import in.codifi.api.service.spec.IDigioService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/digio")
public class DigioController implements IDigioController {
	@Inject
	CommonMethods commonMethods;
	@Inject
	IDigioService digioService;

	/**
	 * Method to intialize digio to open digi locker
	 */
	@Override
	public ResponseModel iniDigio(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = digioService.iniDigio(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method to save address from digio
	 */
	@Override
	public ResponseModel saveDigioAadhar(long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		if (applicationId > 0) {
			responseModel = digioService.saveDigioAadhar(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * Method for web hook to check status for digi locker
	 */
	public ResponseModel whDigilocker(WebhookDigioRequestModel digioRequestModel) {
	    ResponseModel responseModel = new ResponseModel();
	    System.out.println("Digio callback");
	    
	    if (digioRequestModel != null) {
	        try {
	            ObjectMapper obj = new ObjectMapper();
	            String ModelRes = obj.writeValueAsString(digioRequestModel);
	            System.out.println("WebhookDigioRequestModel: " + ModelRes);

	            WebhookDigioPayloadModel payload = digioRequestModel.getPayload();
	            if (payload != null) {
	                WebhookDigilockerRequestModel digilockerRequest = payload.getDigilockerRequest();
	                if (digilockerRequest != null) {
	                    String customerName = digilockerRequest.getCustomerName();
	                    String customerIdentifier = digilockerRequest.getCustomerIdentifier();
	                    String id = digilockerRequest.getId();
	                    String kycRequestId = digilockerRequest.getKycRequestId();
	                    String referenceId = digilockerRequest.getReferenceId();
	                    String transactionId = digilockerRequest.getTransactionId();
	                    String state = digilockerRequest.getState();
	                    List<String> sharedDocuments = digilockerRequest.getSharedDocuments();

	                    System.out.println("Customer Name: " + customerName);
	                    System.out.println("Customer Identifier: " + customerIdentifier);
	                    System.out.println("ID: " + id);
	                    System.out.println("KYC Request ID: " + kycRequestId);
	                    System.out.println("Reference ID: " + referenceId);
	                    System.out.println("Transaction ID: " + transactionId);
	                    System.out.println("State: " + state);
	                    System.out.println("Shared Documents: " + sharedDocuments);
	                }
	            }

	            String currentTime = LocalTime.now().toString();
	            System.out.println("Current Time: " + currentTime);

	            responseModel = digioService.whDigilocker(digioRequestModel);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return responseModel;
	}


}

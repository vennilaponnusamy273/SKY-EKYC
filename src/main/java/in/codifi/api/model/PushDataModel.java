package in.codifi.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushDataModel {

	  private String purpose;
	    private String ver;
	    private String timestamp;
	    private String txnid;
	    private String clienttxnid;
	    private String customerId;
	    private String fipid;
	    private String datarequested;
	    private boolean encryption;
	    private String fipname;
	    private String pan;
	    private String consentid;
	    private String maskedAccountNumber;
	    private String accRefNumber;
	    private String ErrorMessage;
	    private DataDetail dataDetail;
	    @Getter
	    @Setter
	    public static class DataDetail {
	        private String pdfbase64;
	        private String pdfbinary;
	        private JsonData jsonData;
	        private String xmlData;
	        private String csvData;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class JsonData {
	        private Account account;

	        // Getters and setters for the above field
	    }
	    @Getter
	    @Setter
	    public static class Account {
	        private Transactions transactions;
	        private String xmlns;
	        private String xmlns_xsi;
	        private String xsi_schemaLocation;
	        private String linkedAccRef;
	        private String maskedAccNumber;
	        private String version;
	        private String type;
	        private Profile profile;
	        private Summary summary;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class Transactions {
	        private String startDate;
	        private String endDate;
	        private List<Transaction> transaction;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class Transaction {
	        private String txnId;
	        private String type;
	        private String mode;
	        private String amount;
	        private String currentBalance;
	        private String transactionTimestamp;
	        private String valueDate;
	        private String narration;
	        private String reference;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class Profile {
	        private Holders holders;

	        // Getters and setters for the above field
	    }
	    @Getter
	    @Setter
	    public static class Holders {
	        private String type;
	        private Holder holder;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class Holder {
	        private String name;
	        private String address;
	        private String dob;
	        private String mobile;
	        private String nominee;
	        private String email;
	        private String pan;
	        private String ckycCompliance;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class Summary {
	        private String currentBalance;
	        private String currency;
	        private String exchgeRate;
	        private String balanceDateTime;
	        private String type;
	        private String branch;
	        private String ifscCode;
	        private String micrCode;
	        private String drawingLimit;
	        private String currentODLimit;
	        private String status;
	        private Pending pending;
	        private String openingDate;

	        // Getters and setters for the above fields
	    }
	    @Getter
	    @Setter
	    public static class Pending {
	        private String amount;

	        // Getters and setters for the above field
	    }
}

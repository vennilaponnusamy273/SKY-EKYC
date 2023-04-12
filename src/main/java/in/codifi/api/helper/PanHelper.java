package in.codifi.api.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.util.encoders.Base64;
import org.json.simple.JSONObject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.restservice.NsdlPanRestService;
import in.codifi.api.utilities.APIBased.DummyTrustManager;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class PanHelper {

	@Inject
	ApplicationProperties props;
	@Inject
	ApplicationUserRepository repository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	NsdlPanRestService nsdlPanService;

	private static final Logger logger = LogManager.getLogger(PanHelper.class);
	public String getPanDetailsFromNSDL(String panCard, Long applicationId) {
		/**
		 * To create the create the jks file for the given application id
		 */
		pfx2JksFile(applicationId);
		/**
		 * To create the sig file from the jks file
		 */
		pkcs7Generate(applicationId, panCard);
		/**
		 * TO get the result from the NSDL
		 */
		String result = apiCallForPanVerififcation(applicationId, panCard);
		if (result != null && !result.equalsIgnoreCase("")) {
			return result;
		} else {
			return null;
		}
	}

	/**
	 * method to generate pan pfx2JksFile
	 */

	public void pfx2JksFile(Long applicationId) {
		try {
			CommonMethods.trustedManagement();
			String userJksFileLocation = props.getPanFilePath() + applicationId + EkycConstants.UBUNTU_FILE_SEPERATOR
					+ applicationId + EkycConstants.FILE_JKS;
			String args1[] = { props.getPanPfxFileLocation(), props.getPanPfxPassword(), userJksFileLocation };
			File dir = new File(props.getPanFilePath() + applicationId);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (args1.length < 1) {
				System.out.println(MessageConstants.PAN_FILE_USAGE);
				System.exit(1);
			}
			File fileIn = new File(args1[0]);
			File fileOut = null;
			if (args1.length == 3) {
				fileOut = new File(args1[2]);
			} else {
				System.out.println(MessageConstants.PAN_FILE_USAGE);
				System.exit(1);
			}
			if (!fileIn.canRead()) {
				System.out.println(MessageConstants.PAN_KEY_STORE_MSG + fileIn.getPath());
				System.exit(2);
			}
			if (fileOut.exists() && !fileOut.canWrite()) {
				System.out.println(MessageConstants.PAN_FILE_NOT_WRITE + fileOut.getPath());
				System.exit(2);
			}
			KeyStore kspkcs12 = KeyStore.getInstance(EkycConstants.KEY_PKS);
			KeyStore ksjks = KeyStore.getInstance(EkycConstants.KEY_JKS);
			char inphrase[] = args1[1].toCharArray();
			char outphrase[] = args1[1].toCharArray();
			kspkcs12.load(new FileInputStream(fileIn), inphrase);
			ksjks.load(fileOut.exists() ? ((java.io.InputStream) (new FileInputStream(fileOut))) : null, outphrase);
			Enumeration eAliases = kspkcs12.aliases();
			do {
				if (!eAliases.hasMoreElements())
					break;
				String strAlias = (String) eAliases.nextElement();
				if (kspkcs12.isKeyEntry(strAlias)) {
					java.security.Key key = kspkcs12.getKey(strAlias, inphrase);
					Certificate chain[] = kspkcs12.getCertificateChain(strAlias);
					ksjks.setKeyEntry(strAlias, key, outphrase, chain);
				}
			} while (true);
			OutputStream out = new FileOutputStream(fileOut);
			ksjks.store(out, outphrase);
			out.close();
			System.out.println(MessageConstants.PAN_KEYSTORE_SUC_MSG);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
	}

	/**
	 * method to generate pan pkcs7Generate
	 */

	public void pkcs7Generate(Long applicationId, String panCard) {
		try {
			CommonMethods.trustedManagement();
			String data = props.getPanPfxUserId() + "^" + panCard;
			String jksFileLocation = props.getPanFilePath() + applicationId + EkycConstants.UBUNTU_FILE_SEPERATOR
					+ applicationId + EkycConstants.FILE_JKS;
			String signatureFile = props.getPanFilePath() + applicationId + EkycConstants.UBUNTU_FILE_SEPERATOR
					+ EkycConstants.OUTPUT_SIG;
			String args1[] = { jksFileLocation, props.getPanPfxPassword(), data, signatureFile };
			if (args1.length < 3) {
				System.out.println(MessageConstants.PAN_PKCS7GEN);
				System.exit(1);
			}

			KeyStore keystore = KeyStore.getInstance(EkycConstants.KEY_JKS);
			InputStream input = new FileInputStream(args1[0]);
			try {
				char[] password = args1[1].toCharArray();
				keystore.load(input, password);
			} catch (IOException e) {
			} finally {

			}
			Enumeration e = keystore.aliases();
			String alias = "";

			if (e != null) {
				while (e.hasMoreElements()) {
					String n = (String) e.nextElement();
					if (keystore.isKeyEntry(n)) {
						alias = n;
					}
				}
			}
			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, args1[1].toCharArray());
			X509Certificate myPubCert = (X509Certificate) keystore.getCertificate(alias);
			byte[] dataToSign = args1[2].getBytes();
			CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			sgen.addSigner(privateKey, myPubCert, CMSSignedDataGenerator.DIGEST_SHA1);
			Certificate[] certChain = keystore.getCertificateChain(alias);
			ArrayList certList = new ArrayList();
			CertStore certs = null;
			for (int i = 0; i < certChain.length; i++)
				certList.add(certChain[i]);
			sgen.addCertificatesAndCRLs(CertStore.getInstance(EkycConstants.SIGN_COLL,
					new CollectionCertStoreParameters(certList), EkycConstants.SIGN_BC));
			CMSSignedData csd = sgen.generate(new CMSProcessableByteArray(dataToSign), true, "BC");
			byte[] signedData = csd.getEncoded();
			byte[] signedData64 = Base64.encode(signedData);
			FileOutputStream out = new FileOutputStream(args1[EkycConstants.FILE_ARGS]);
			out.write(signedData64);
			out.close();
			System.out.println(MessageConstants.PAN_SIGN_OUT + args1[EkycConstants.FILE_ARGS]);

		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
	}

	/**
	 * method to call the apiCallForPanVerififcation
	 */

	public String apiCallForPanVerififcation(Long applicationId, String panCard) {
		String result = "";
		BufferedWriter out = null;
		URL url = null;
		try {
			CommonMethods.trustedManagement();
			String data = null;
			String signature = null;
			final String version = props.getPanVersion();
			Date startTime = null;
			Calendar c1 = Calendar.getInstance();
			startTime = c1.getTime();
			Date connectionStartTime = null;
			String logMsg = "\n-";
			FileWriter fstream = null;
			Calendar c = Calendar.getInstance();
			long nonce = c.getTimeInMillis();
			try {
				data = props.getPanPfxUserId() + "^" + panCard;
				/**
				 * read the signature file for the user and assign for the user
				 */
				signature = new String(Files.readAllBytes(Paths.get(props.getPanFilePath() + applicationId
						+ EkycConstants.UBUNTU_FILE_SEPERATOR + EkycConstants.OUTPUT_SIG)));
			} catch (Exception e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
			}
			try {
				File f = new File(props.getPanLogsUrl());
				if (!f.exists()) {
					f.mkdir();
				}
				fstream = new FileWriter(props.getPanLogsUrl(), true);
				out = new BufferedWriter(fstream);
			} catch (Exception e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
				System.out.println(logMsg);
				out.write(logMsg);
				out.close();
			}
			SSLContext sslcontext = null;
			try {
				sslcontext = SSLContext.getInstance(EkycConstants.SSL);
				sslcontext.init(new KeyManager[0], new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
				e.printStackTrace(System.err);
				out.write(logMsg);
				out.close();
			} catch (KeyManagementException e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
				e.printStackTrace(System.err);
				out.write(logMsg);
				out.close();
			}
			SSLSocketFactory factory = sslcontext.getSocketFactory();
			result = nsdlPanService.GetNSdlDEtails(data, signature, version);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		} finally {

		}
		return result;
	}

	public ResponseModel saveResult(String result, ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
		try {
		if (result != null && !result.isEmpty()) {
			JSONObject tempResult = stringToJson(result);
			if (tempResult.containsKey(EkycConstants.PAN_FIRSTNAME)) {
				String firstName = (String) tempResult.get(EkycConstants.PAN_FIRSTNAME);
				String panNumber = (String) tempResult.get(EkycConstants.PAN_CARD);
				String middleName = (String) tempResult.get(EkycConstants.PAN_MIDDLENAME);
				String lastName = (String) tempResult.get(EkycConstants.PAN_LASTNAME);
				String panCardname = firstName + " " + middleName + " " + lastName;
				String lastUpdatedDate = (String) tempResult.get(EkycConstants.PAN_LAST_UPDATED_DATE);
				String nameOnCard = (String) tempResult.get(EkycConstants.PAN_NAMEONCARD);
				String aathar_status = (String) tempResult.get(EkycConstants.PAN_AADHAR_STATUS);
				Optional<ApplicationUserEntity> isUserPresent = repository.findById(userEntity.getId());
				if (isUserPresent.isPresent()) {
					ApplicationUserEntity updatedUserDetails = null;
					ApplicationUserEntity oldUserEntity = isUserPresent.get();
					oldUserEntity.setFirstName(firstName);
					oldUserEntity.setLastName(lastName);
					oldUserEntity.setMiddleName(middleName);
					oldUserEntity.setUserName(panCardname);
					oldUserEntity.setPanNumber(panNumber);
					oldUserEntity.setStatus(EkycConstants.EKYC_STATUS_INPROGRESS);
					updatedUserDetails = repository.save(oldUserEntity);
					commonMethods.UpdateStep(EkycConstants.PAGE_PAN_NSDL_DATA_CONFIRM, userEntity.getId());
					responseModel = new ResponseModel();
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
					responseModel.setResult(updatedUserDetails);
					responseModel.setPage(EkycConstants.PAGE_PAN_CONFIRM);
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_PAN_MSG);
			}
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	/**
	 * method to convert the StringToJson Response
	 */

	public static JSONObject stringToJson(String nsdlResponse) {
		JSONObject response = new JSONObject();
		try {
		String[] resp = null;
		if (nsdlResponse.lastIndexOf("^") == nsdlResponse.length() - 1) {
			resp = nsdlResponse.split("\\^");
		} else {
			resp = nsdlResponse.split("\\^");
		}
		System.out.println(resp.length);
		if (resp.length > 3) {
			String panCardStatus = resp[2];
			if (panCardStatus.equalsIgnoreCase("E")) {
				response.put("responseCode", resp[0]);
				if (resp.length > 1) {
					response.put(EkycConstants.PAN_CARD, resp[1]);
				}
				if (resp.length > 2) {
					response.put(EkycConstants.PAN_CARD_STATUS, resp[2]);
				}
				if (resp.length > 3) {
					response.put(EkycConstants.PAN_LASTNAME, resp[3]);
				}
				if (resp.length > 4) {
					response.put(EkycConstants.PAN_FIRSTNAME, resp[4]);
				}
				if (resp.length > 5) {
					response.put(EkycConstants.PAN_MIDDLENAME, resp[5]);
				}
				if (resp.length > 6) {
					response.put(EkycConstants.PAN_TITLE, resp[6]);
				}
				if (resp.length > 7) {
					response.put(EkycConstants.PAN_LAST_UPDATED_DATE, resp[7]);
				}
				if (resp.length > 8) {
					response.put(EkycConstants.PAN_NAMEONCARD, resp[8]);
				}
				if (resp.length > 9) {
					response.put(EkycConstants.PAN_AADHAR_STATUS, resp[9]);
				}
			} else {
				response.put("stat", EkycConstants.FAILED_STATUS);
				response.put("message", EkycConstants.FAILED_MSG);
				response.put("panCardStatus", MessageConstants.INVALID_PAN_MSG);
			}
		} else {
			response.put("stat", EkycConstants.FAILED_STATUS);
			response.put("message", EkycConstants.FAILED_MSG);
			response.put("responseCode", resp[0]);
			response.put("panCard", resp[1]);
			response.put("panCardStatus", MessageConstants.INVALID_PAN_MSG);
		}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
		}
		return response;
	}
}

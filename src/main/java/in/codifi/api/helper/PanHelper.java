package in.codifi.api.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
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

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.util.encoders.Base64;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.hazelcast.topic.Message;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ErpExistingApiModel;
import in.codifi.api.model.ExistingCustReqModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.restservice.ErpRestService;
import in.codifi.api.utilities.APIBased.DummyHostnameVerifier;
import in.codifi.api.utilities.APIBased.DummyTrustManager;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@Service
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class PanHelper {

	@Inject
	ApplicationProperties props;
	@Inject
	ApplicationUserRepository repository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ErpRestService erpRestService;

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
			e.printStackTrace();
		}
		// return MessageConstants.KEYSTORE_SUCS_MSG;
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
			e.printStackTrace();
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
				// signature = CSEnvVariables.getProperty("signature");
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
			String urlParameters = EkycConstants.CONSTANT_URL_DATA;
			try {
				urlParameters = urlParameters + URLEncoder.encode(data, EkycConstants.CONSTANT_URL_UF8)
						+ EkycConstants.CONSTANT_SIGNATURE
						+ URLEncoder.encode(signature, EkycConstants.CONSTANT_URL_UF8) + EkycConstants.CONSTANT_VERSION
						+ URLEncoder.encode(version, EkycConstants.CONSTANT_URL_UF8);
			} catch (Exception e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
				e.printStackTrace();
				out.write(logMsg);
				out.close();
			}
			try {
				HttpsURLConnection connection;
				InputStream is = null;
				String ip = props.getPanNsdlUrl();
				url = new URL(ip);
				connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestMethod(EkycConstants.HTTP_POST);
				connection.setRequestProperty(EkycConstants.CONSTANT_CONTENT_TYPE, EkycConstants.CONSTANT_URL_ENCODED);
				connection.setRequestProperty(EkycConstants.CONSTANT_CONTENT_LENGTH,
						"" + Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty(EkycConstants.CONSTANT_CONTENT_LENGTH,
						"" + Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty(EkycConstants.CONSTANT_CONTENT_LANG, EkycConstants.LAG_ENG_US);
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setSSLSocketFactory(factory);
				connection.setHostnameVerifier(new DummyHostnameVerifier());
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				osw.write(urlParameters);
				osw.flush();
				connectionStartTime = new Date();
				logMsg += EkycConstants.LOG_MSG_REQ + connectionStartTime;
				logMsg += EkycConstants.LOG_MSG_DATA + data;
				logMsg += EkycConstants.LOG_MSG_VERSION + version;
				osw.close();
				is = connection.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				String line = null;
				line = in.readLine();
				result = line;
				System.out.println(EkycConstants.CONN_OUTPUT + line);
				is.close();
				in.close();
			} catch (ConnectException e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
				out.write(logMsg);
				out.close();
			} catch (Exception e) {
				logMsg += MessageConstants.PAN_EXE_MSG + e.getMessage() + MessageConstants.PAN_PRG_SRT_TIME + startTime
						+ MessageConstants.PAN_PRG_NO + nonce;
				out.write(logMsg);
				out.close();
				e.printStackTrace();
			}
			out.write(logMsg);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return result;
	}

	public ResponseModel saveResult(String result, ApplicationUserEntity userEntity) {
		ResponseModel responseModel = new ResponseModel();
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
					commonMethods.UpdateStep(2.1, userEntity.getId());
					ExistingCustReqModel custModel = new ExistingCustReqModel();
					custModel.setInput(oldUserEntity.getEmailId());
					custModel.setInputType(EkycConstants.ERP_PAN);
					ErpExistingApiModel existingModel = erpRestService.erpCheckExisting(custModel);
					if (existingModel != null && StringUtil.isNotNullOrEmpty(existingModel.getExisting())
							&& StringUtil.isEqual(existingModel.getExisting(), EkycConstants.EXISTING_YES)
							&& StringUtil.isNotNullOrEmpty(existingModel.getStatus())
							&& StringUtil.isNotEqual(existingModel.getExisting(), EkycConstants.STATUS_INACTIVE)) {
						if (StringUtil.isNotNullOrEmpty(existingModel.getStatus())
								&& StringUtil.isEqual(existingModel.getExisting(), EkycConstants.STATUS_ACTIVE)) {
							responseModel = commonMethods.constructFailedMsg(MessageConstants.EKYC_ACTIVE_CUSTOMER);
						} else if (StringUtil.isNotNullOrEmpty(existingModel.getStatus())
								&& StringUtil.isEqual(existingModel.getExisting(), EkycConstants.STATUS_DORMANT)) {
							responseModel = commonMethods.constructFailedMsg(MessageConstants.EKYC_DORMANT_CUSTOMER);
							responseModel.setPage(EkycConstants.PAGE_PDFDOWNLOAD);
						}
					} else {
						responseModel = new ResponseModel();
						responseModel.setMessage(EkycConstants.SUCCESS_MSG);
						responseModel.setStat(EkycConstants.SUCCESS_STATUS);
						responseModel.setResult(updatedUserDetails);
						responseModel.setPage(EkycConstants.PAGE_PAN_NSDL_DATA_CONFIRM);
					}
				}
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_PAN_MSG);
			}
		}
		return responseModel;
	}

	/**
	 * method to convert the StringToJson Response
	 */

	public static JSONObject stringToJson(String nsdlResponse) {
		JSONObject response = new JSONObject();
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
		return response;
	}
}

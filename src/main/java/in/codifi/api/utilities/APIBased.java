package in.codifi.api.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class APIBased {
	public static class DummyTrustManager implements X509TrustManager {

		public DummyTrustManager() {
		}

		public boolean isClientTrusted(X509Certificate cert[]) {
			return true;
		}

		public boolean isServerTrusted(X509Certificate cert[]) {
			return true;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}
	}

	public static class DummyHostnameVerifier implements HostnameVerifier {

		public boolean verify(String urlHostname, String certHostname) {
			return true;
		}

		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
	}

	static Properties probConfig = null;
	static InputStream probsInStream = null;

	public static String getProperty(String propertyName) {
		if (null == probConfig) {
			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				probsInStream = classLoader.getResourceAsStream("props.ini");
				probConfig = new Properties();
				probConfig.load(probsInStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return probConfig.getProperty(propertyName);
	}
}

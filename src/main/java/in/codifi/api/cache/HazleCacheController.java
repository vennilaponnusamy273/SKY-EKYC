package in.codifi.api.cache;

import java.util.Map;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazleCacheController {
	public static HazleCacheController HazleCacheController = null;
	private HazelcastInstance hz = null;

	public static HazleCacheController getInstance() {
		if (HazleCacheController == null) {
			HazleCacheController = new HazleCacheController();

		}
		return HazleCacheController;
	}

	public HazelcastInstance getHz() {
		if (hz == null) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.setClusterName("codifi"); // dev --default codifi
			clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701");
			hz = HazelcastClient.newHazelcastClient(clientConfig);
		}
		return hz;
	}

	IMap<String, Integer> resendOtp = getHz().getMap("resendOtp"); // 30 seconds for both sms and email
	IMap<String, Integer> retryOtp = getHz().getMap("retryOtp"); // five minutes for both sms and email
	IMap<String, Integer> verifyOtp = getHz().getMap("verifyOtp");
	private Map<String, String> keycloakAdminSession = getHz().getMap("keycloakAdminSession");
	private Map<String, String> kraKeyValue = getHz().getMap("kraKeyValue");
	private Map<Integer, String> pageDetail = getHz().getMap("pageDetail");

	public IMap<String, Integer> getRetryOtp() {
		return retryOtp;
	}

	public void setRetryOtp(IMap<String, Integer> retryOtp) {
		this.retryOtp = retryOtp;
	}

	public IMap<String, Integer> getVerifyOtp() {
		return verifyOtp;
	}

	public void setVerifyOtp(IMap<String, Integer> verifyOtp) {
		this.verifyOtp = verifyOtp;
	}

	public IMap<String, Integer> getResendOtp() {
		return resendOtp;
	}

	public void setResendOtp(IMap<String, Integer> resendOtp) {
		this.resendOtp = resendOtp;
	}

	public Map<String, String> getKeycloakAdminSession() {
		return keycloakAdminSession;
	}

	public void setKeycloakAdminSession(Map<String, String> keycloakAdminSession) {
		this.keycloakAdminSession = keycloakAdminSession;
	}

	public Map<String, String> getKraKeyValue() {
		return kraKeyValue;
	}

	public void setKraKeyValue(Map<String, String> kraKeyValue) {
		this.kraKeyValue = kraKeyValue;
	}

	public Map<Integer, String> getPageDetail() {
		return pageDetail;
	}

	public void setPageDetail(Map<Integer, String> pageDetail) {
		this.pageDetail = pageDetail;
	}

}

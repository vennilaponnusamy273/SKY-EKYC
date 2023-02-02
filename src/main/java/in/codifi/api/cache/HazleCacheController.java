package in.codifi.api.cache;

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

	IMap<String, Integer> failedOtp = getHz().getMap("failedOtp");
	IMap<String, Integer> retryOtp = getHz().getMap("retryOtp");
	IMap<String, Integer> verifyOtp = getHz().getMap("verifyOtp");

	public IMap<String, Integer> getFailedOtp() {
		return failedOtp;
	}

	public void setFailedOtp(IMap<String, Integer> failedOtp) {
		this.failedOtp = failedOtp;
	}

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

}

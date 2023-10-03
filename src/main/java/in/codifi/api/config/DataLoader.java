package in.codifi.api.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.KraKeyValueEntity;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.ServicesAccessRepository;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DataLoader extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	KraKeyValueRepository keyValueRepository;

	@Inject
	ServicesAccessRepository serviceAccessRepository;

	public void init(@Observes StartupEvent ev) throws ServletException {
		reloadHazleCache();
		reloadPageDetails();
		getAllExternalEntities();
	}

	public void reloadHazleCache() {
		HazleCacheController.getInstance().getKraKeyValue().clear();
		Iterable<KraKeyValueEntity> list = keyValueRepository.findAll();
		for (KraKeyValueEntity entity : list) {
			if (entity != null) {
				HazleCacheController.getInstance().getKraKeyValue().put(
						entity.getMasterId() + "_" + entity.getMasterName() + "_" + entity.getKraKey(),
						entity.getKraValue());
			}
		}
		System.out.println(HazleCacheController.getInstance().getKraKeyValue().size());
	}

	public void reloadPageDetails() {
		HazleCacheController.getInstance().getPageDetail().clear();
		int count = 1;
		HazleCacheController.getInstance().getPageDetail().put(count++, "1"); // PAGE_EMAIL
		HazleCacheController.getInstance().getPageDetail().put(count++, "1.1");// PAGE_PASSWORD
		HazleCacheController.getInstance().getPageDetail().put(count++, "2");// PAGE_PAN
		HazleCacheController.getInstance().getPageDetail().put(count++, "2.1");// PAGE_PAN_NSDL_DATA_CONFIRM
		HazleCacheController.getInstance().getPageDetail().put(count++, "2.2");// PAGE_PAN_CONFIRM
		HazleCacheController.getInstance().getPageDetail().put(count++, "2.3");// PAGE_PAN_KRA_DOB_ENTRY
		HazleCacheController.getInstance().getPageDetail().put(count++, "3");// PAGE_AADHAR
		HazleCacheController.getInstance().getPageDetail().put(count++, "4");// PAGE_PROFILE
		HazleCacheController.getInstance().getPageDetail().put(count++, "5");// PAGE_BANK
		HazleCacheController.getInstance().getPageDetail().put(count++, "5.1");// PAGE_PENNY
		HazleCacheController.getInstance().getPageDetail().put(count++, "6");// PAGE_SEGMENT
		HazleCacheController.getInstance().getPageDetail().put(count++, "7");// PAGE_PAYMENT
		HazleCacheController.getInstance().getPageDetail().put(count++, "8");// PAGE_NOMINEE
		HazleCacheController.getInstance().getPageDetail().put(count++, "8.1");// PAGE_NOMINEE_1
		HazleCacheController.getInstance().getPageDetail().put(count++, "8.2");// PAGE_NOMINEE_2
		HazleCacheController.getInstance().getPageDetail().put(count++, "8.3");// PAGE_NOMINEE_3
		HazleCacheController.getInstance().getPageDetail().put(count++, "9");// PAGE_DOCUMENT
		HazleCacheController.getInstance().getPageDetail().put(count++, "10");// PAGE_IPV
		HazleCacheController.getInstance().getPageDetail().put(count++, "11");// PAGE_PDFDOWNLOAD
		HazleCacheController.getInstance().getPageDetail().put(count++, "12");// PAGE_ESIGN
		HazleCacheController.getInstance().getPageDetail().put(count++, "13");// PAGE_COMPLETED_EMAIL_ATTACHED
		HazleCacheController.getInstance().getPageDetail().put(count++, "14");// PAGE COMPLETED
		System.out.println(HazleCacheController.getInstance().getPageDetail().size());
	}


	public void getAllExternalEntities() {
//		try {
//			HazleCacheController.getInstance().getExtService().clear();
//			Iterable<ServicesEntity> externalEntities = serviceAccessRepository.findAll();
//			for (ServicesEntity entity : externalEntities) {
//				if (entity != null) {
//					String key = entity.getService();
//					Integer value = entity.getAccess();
//					if (HazleCacheController.getInstance().getExtService().containsKey(value.toString())) {
//						System.out.println("Key " + value + " already exists. Value will be overwritten.");
//					}
//					Integer previousValue = HazleCacheController.getInstance().getExtService().put(key, value);
//					System.out.println("Added key-value pair: " + key + " - " + value);
//					if (previousValue != null) {
//						System.out.println("Previous value: " + previousValue);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}

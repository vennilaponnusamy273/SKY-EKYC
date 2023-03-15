package in.codifi.api.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.KraKeyValueEntity;
import in.codifi.api.repository.KraKeyValueRepository;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DataLoader extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	KraKeyValueRepository keyValueRepository;

	public void init(@Observes StartupEvent ev) throws ServletException {
		reloadHazleCache();
		reloadPageDetails();
//		Iterable<KraKeyValueEntity> list = keyValueRepository.findAll();
//		for (KraKeyValueEntity entity : list) {
//			if (entity != null) {
//				HazleCacheController.getInstance().getKraKeyValue().put(
//						entity.getMasterId() + "_" + entity.getMasterName() + "_" + entity.getKraKey(),
//						entity.getKraValue());
//			}
//		}
//		System.out.println(HazleCacheController.getInstance().getKraKeyValue().size());

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
		HazleCacheController.getInstance().getPageDetail().put(1, "1"); // PAGE_EMAIL
		HazleCacheController.getInstance().getPageDetail().put(2, "1.1");// PAGE_PASSWORD
		HazleCacheController.getInstance().getPageDetail().put(3, "2");// PAGE_PAN
		HazleCacheController.getInstance().getPageDetail().put(4, "2.1");// PAGE_PAN_NSDL_DATA_CONFIRM
		HazleCacheController.getInstance().getPageDetail().put(5, "2.2");// PAGE_PAN_CONFIRM
		HazleCacheController.getInstance().getPageDetail().put(6, "2.3");// PAGE_PAN_KRA_DOB_ENTRY
		HazleCacheController.getInstance().getPageDetail().put(7, "3");// PAGE_AADHAR
		HazleCacheController.getInstance().getPageDetail().put(8, "4");// PAGE_PROFILE
		HazleCacheController.getInstance().getPageDetail().put(9, "5");// PAGE_BANK
		HazleCacheController.getInstance().getPageDetail().put(10, "6");// PAGE_SEGMENT
		HazleCacheController.getInstance().getPageDetail().put(11, "7");// PAGE_PAYMENT
		HazleCacheController.getInstance().getPageDetail().put(12, "8");// PAGE_NOMINEE
		HazleCacheController.getInstance().getPageDetail().put(13, "8.1");// PAGE_NOMINEE_1
		HazleCacheController.getInstance().getPageDetail().put(14, "8.2");// PAGE_NOMINEE_2
		HazleCacheController.getInstance().getPageDetail().put(15, "8.3");// PAGE_NOMINEE_3
		HazleCacheController.getInstance().getPageDetail().put(16, "9");// PAGE_DOCUMENT
		HazleCacheController.getInstance().getPageDetail().put(17, "10");// PAGE_IPV
		HazleCacheController.getInstance().getPageDetail().put(18, "11");// PAGE_PDFDOWNLOAD
		HazleCacheController.getInstance().getPageDetail().put(19, "12");// PAGE_ESIGN
		HazleCacheController.getInstance().getPageDetail().put(20, "13");// PAGE_COMPLETED_EMAIL_ATTACHED
		System.out.println(HazleCacheController.getInstance().getPageDetail().size());
	};
}

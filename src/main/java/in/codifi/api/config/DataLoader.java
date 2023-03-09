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
}

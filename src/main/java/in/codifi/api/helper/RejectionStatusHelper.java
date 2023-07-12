package in.codifi.api.helper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;

import in.codifi.api.entity.ApiStatusArchiveEntity;
import in.codifi.api.entity.ApiStatusEntity;
import in.codifi.api.repository.ApiStatusArchiveRepository;
import in.codifi.api.repository.ApiStatusRepository;

@ApplicationScoped
public class RejectionStatusHelper {

	@Inject
	ApiStatusRepository apiStatusRepository;
	@Inject
	ApiStatusArchiveRepository apiStatusArchiveRepository;

	public ApiStatusArchiveEntity insertArchiveTableRecord(long applicationId, String stage) {
		ApiStatusArchiveEntity updatedEntity = null;
		try {
			ApiStatusEntity apiStatusEntity = apiStatusRepository.findByApplicationIdAndStage(applicationId, stage);
			if (apiStatusEntity != null) {
				System.out.println("rejection update check");
				ApiStatusArchiveEntity apiStatusArchiveEntity = new ApiStatusArchiveEntity();
				BeanUtils.copyProperties(apiStatusArchiveEntity, apiStatusEntity);
				apiStatusArchiveEntity.setId(null);
				updatedEntity = apiStatusArchiveRepository.save(apiStatusArchiveEntity);
				apiStatusRepository.deleteById(apiStatusEntity.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedEntity;
	}

}

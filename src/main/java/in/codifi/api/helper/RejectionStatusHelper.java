package in.codifi.api.helper;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;

import in.codifi.api.entity.ApiStatusArchiveEntity;
import in.codifi.api.entity.ApiStatusEntity;
import in.codifi.api.repository.ApiStatusArchiveRepository;
import in.codifi.api.repository.ApiStatusRepository;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class RejectionStatusHelper {

	@Inject
	ApiStatusRepository apiStatusRepository;
	@Inject
	ApiStatusArchiveRepository apiStatusArchiveRepository;

	public ApiStatusArchiveEntity insertArchiveTableRecord(long applicationId, String stage) {
		ApiStatusArchiveEntity updatedEntity = null;
		try {
			if (StringUtil.isNotEqual(stage, EkycConstants.PAGE_DOCUMENT)) {
				ApiStatusEntity apiStatusEntity = apiStatusRepository
						.findByApplicationIdAndStageAndStatus(applicationId, stage, 0);
				if (apiStatusEntity != null) {
					ApiStatusArchiveEntity apiStatusArchiveEntity = new ApiStatusArchiveEntity();
					BeanUtils.copyProperties(apiStatusArchiveEntity, apiStatusEntity);
					apiStatusArchiveEntity.setId(null);
					updatedEntity = apiStatusArchiveRepository.save(apiStatusArchiveEntity);
					apiStatusRepository.deleteById(apiStatusEntity.getId());
				}
			} else {
				List<ApiStatusEntity> docResult = apiStatusRepository.getResultForDoc(applicationId, stage, 0);
				if (StringUtil.isListNotNullOrEmpty(docResult)) {
					for (ApiStatusEntity apiStatusEntity : docResult) {
						ApiStatusArchiveEntity apiStatusArchiveEntity = new ApiStatusArchiveEntity();
						BeanUtils.copyProperties(apiStatusArchiveEntity, apiStatusEntity);
						apiStatusArchiveEntity.setId(null);
						updatedEntity = apiStatusArchiveRepository.save(apiStatusArchiveEntity);
						apiStatusRepository.deleteById(apiStatusEntity.getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedEntity;
	}

}

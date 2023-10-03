package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ApiStatusArchiveEntity;

public interface ApiStatusArchiveRepository extends CrudRepository<ApiStatusArchiveEntity, Long> {

	List<ApiStatusArchiveEntity> findByApplicationId(Long applicationId);

	ApiStatusArchiveEntity findByApplicationIdAndStage(Long applicationId, String stage);

	ApiStatusArchiveEntity findByApplicationIdAndStageAndDocType(Long applicationId, String stage, String docType);

	List<ApiStatusArchiveEntity> findByApplicationIdAndStatus(Long applicationId, int status);

}

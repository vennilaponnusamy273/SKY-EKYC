package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.ApiStatusEntity;

public interface ApiStatusRepository extends CrudRepository<ApiStatusEntity, Long> {

	List<ApiStatusEntity> findByApplicationId(Long applicationId);

	ApiStatusEntity findByApplicationIdAndStage(Long applicationId, String stage);

	ApiStatusEntity findByApplicationIdAndStageAndDocType(Long applicationId, String stage, String docType);

	List<ApiStatusEntity> findByApplicationIdAndStatus(Long applicationId, int status);
}

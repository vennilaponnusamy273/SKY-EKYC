package in.codifi.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.ApiStatusEntity;

public interface ApiStatusRepository extends CrudRepository<ApiStatusEntity, Long> {

	List<ApiStatusEntity> findByApplicationId(Long applicationId);

	ApiStatusEntity findByApplicationIdAndStage(Long applicationId, String stage);

	ApiStatusEntity findByApplicationIdAndStageAndStatus(Long applicationId, String stage, int status);

	@Transactional
	@Query(value = " SELECT A FROM tbl_api_status  A WHERE A.applicationId =:applicationId and stage =:stage and status =:status ")
	List<ApiStatusEntity> getResultForDoc(@Param("applicationId") long applicationId, @Param("stage") String stage,
			@Param("status") int status);

	ApiStatusEntity findByApplicationIdAndStageAndDocType(Long applicationId, String stage, String docType);

	List<ApiStatusEntity> findByApplicationIdAndStatus(Long applicationId, int status);
}

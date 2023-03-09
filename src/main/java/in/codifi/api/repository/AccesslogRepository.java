package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.AccesslogEntity;

public interface AccesslogRepository extends CrudRepository<AccesslogEntity, Long> {

	AccesslogEntity findByApplicationIdAndType(long applicationId, String method);

	void deleteByApplicationId(long applicationId);

}

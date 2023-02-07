package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ReqResEntity;

public interface ReqResRepository extends CrudRepository<ReqResEntity, Long> {

	ReqResEntity findByApplicationId(Long applicationId);

	ReqResEntity findByApplicationIdAndType(Long applicationId, String type);

}

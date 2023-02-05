package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.NomineeEntity;

public interface NomineeRepository extends CrudRepository<NomineeEntity, Long> {

	List<NomineeEntity> findByapplicationId(Long applicationId);

}

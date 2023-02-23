package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.PennyDropEntity;

public interface PennyDropRepository extends CrudRepository<PennyDropEntity, Long> {
	
	PennyDropEntity findByapplicationId(Long applicationId);

}

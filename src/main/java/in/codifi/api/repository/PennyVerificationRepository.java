package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.PennyVerificationResponseEntity;

public interface PennyVerificationRepository extends CrudRepository<PennyVerificationResponseEntity, Long> {

	PennyVerificationResponseEntity findByapplicationId(Long applicationId);
}

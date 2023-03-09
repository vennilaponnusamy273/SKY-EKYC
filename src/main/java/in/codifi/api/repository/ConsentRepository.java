package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ConsentEntity;

public interface ConsentRepository extends CrudRepository<ConsentEntity, Long> {

	ConsentEntity findByApplicationId(Long applicationId);

	ConsentEntity findByApplicationIdAndConsentType(Long applicationId, String consentType);
}

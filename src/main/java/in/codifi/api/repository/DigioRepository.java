package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.DigioEntity;

public interface DigioRepository extends CrudRepository<DigioEntity, Long> {

	DigioEntity findByapplicationId(Long applicationId);

	DigioEntity findByMobileNoAndRequestId(String mobileNo, String requestId);
}

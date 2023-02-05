package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.AddressEntity;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

	AddressEntity findByapplicationId(Long applicationId);
}

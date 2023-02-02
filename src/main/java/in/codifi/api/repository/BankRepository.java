package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.BankEntity;

public interface BankRepository extends CrudRepository<BankEntity, Long> {

	BankEntity findByapplicationId(Long applicationId);
}

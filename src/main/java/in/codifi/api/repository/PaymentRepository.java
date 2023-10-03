package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.PaymentEntity;

public interface PaymentRepository extends CrudRepository<PaymentEntity, Long> {

	PaymentEntity findByApplicationId(Long applicationId);

	void deleteByApplicationId(long applicationId);
}

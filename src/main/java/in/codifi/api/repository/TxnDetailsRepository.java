package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.TxnDetailsEntity;

public interface TxnDetailsRepository extends CrudRepository<TxnDetailsEntity, Long> {

	TxnDetailsEntity findByapplicationId(Long applicationId);
	TxnDetailsEntity findBytxnId(String txnId);

}

package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.DocumentEntity;

public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {

	DocumentEntity findByApplicationId(Long applicationId);

	Long countByApplicationId(Long applicationid);

	DocumentEntity findByApplicationIdAndTypeOfProof(long applicationId, String typeOfProof);

}

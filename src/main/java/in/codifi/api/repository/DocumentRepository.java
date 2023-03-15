package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.DocumentEntity;

public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {

	List<DocumentEntity> findByApplicationId(Long applicationId);

	Long countByApplicationId(Long applicationid);

	DocumentEntity findByApplicationIdAndDocumentType(long applicationId, String documentType);
	
	void deleteByApplicationId(long applicationId);

}

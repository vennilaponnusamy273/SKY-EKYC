package in.codifi.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.DocumentEntity;

public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {

	List<DocumentEntity> findByApplicationId(Long applicationId);

	Long countByApplicationId(Long applicationid);

	DocumentEntity findByApplicationIdAndDocumentType(long applicationId, String documentType);
	
	void deleteByApplicationId(long applicationId);
	
	@Transactional 
	@Query(value = "SELECT u FROM tbl_document_details u WHERE TIMESTAMPDIFF(MINUTE, u.createdOn, CURRENT_TIMESTAMP) < 5")
    List<DocumentEntity> findRecentlyDocUsers();
}

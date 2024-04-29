package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.CamsEntity;

public interface CamsRepository extends CrudRepository<CamsEntity, Long> {
	
	CamsEntity findByapplicationId(Long applicationId);
	
	CamsEntity findByclientTxtId(String clienttxtid);

}

package in.codifi.api.repository;
import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ResponseCkyc;

public interface CkycResponseRepos extends CrudRepository<ResponseCkyc,Long> {

	ResponseCkyc findByApplicationId(Long applicationId);
	
	void deleteByApplicationId(long applicationId);
}

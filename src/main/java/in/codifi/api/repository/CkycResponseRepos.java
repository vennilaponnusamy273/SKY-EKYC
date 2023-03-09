package in.codifi.api.repository;
import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ResponseCkyc;

public interface CkycResponseRepos extends CrudRepository<ResponseCkyc,Long> {

	ResponseCkyc findByapplicationId(Long applicationid);
	
	void deleteByApplicationId(long applicationId);
}

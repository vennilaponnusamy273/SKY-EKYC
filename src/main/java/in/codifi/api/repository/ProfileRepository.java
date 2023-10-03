package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ProfileEntity;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Long> {

	ProfileEntity findByapplicationId(Long applicationId);
	
	void deleteByApplicationId(long applicationId);
}

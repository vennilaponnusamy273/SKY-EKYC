package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.IvrEntity;

public interface IvrRepository extends CrudRepository<IvrEntity, Long> {

	IvrEntity findByApplicationId(Long applicationId);

	void deleteByApplicationId(long applicationId);

}

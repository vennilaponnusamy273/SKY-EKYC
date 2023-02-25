package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.ApplicationUserEntity;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUserEntity, Long> {

	ApplicationUserEntity findByMobileNo(@Param("mobileNo") Long mobileNumber);

	ApplicationUserEntity findByEmailId(@Param("emailId") String emailId);

	ApplicationUserEntity findByPanNumber(@Param("panNumber") String panNumber);
}

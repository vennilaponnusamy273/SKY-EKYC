package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

	UserEntity findByMobileNo(@Param("mobileNo") Long mobileNumber);

	UserEntity findByEmailId(@Param("emailId") String emailId);
}

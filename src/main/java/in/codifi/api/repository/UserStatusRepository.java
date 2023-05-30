package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.UserStatus;

public interface UserStatusRepository extends CrudRepository<UserStatus, Long> {
	UserStatus findByApplicationIdAndStageAndStatus(long applicationId, String stage, String status);
}

package in.codifi.api.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.UserStatus;

public interface UserStatusRepository extends CrudRepository<UserStatus, Long> {
	UserStatus findByApplicationIdAndStageAndStatus(long applicationId, String stage, String status);
	
	@Transactional
	@Query(value = "SELECT u FROM tbl_user_status u WHERE u.stage <= 12 AND u.status = 'online' AND u.updatedOn <= :currentTimeMinusInterval")
	List<UserStatus> findByOnlineUsers(@Param("currentTimeMinusInterval") Date currentTimeMinusInterval);

}

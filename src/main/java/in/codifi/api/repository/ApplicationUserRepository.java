package in.codifi.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.ApplicationUserEntity;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUserEntity, Long> {

	ApplicationUserEntity findByMobileNo(@Param("mobileNo") Long mobileNumber);

	ApplicationUserEntity findByEmailId(@Param("emailId") String emailId);

	ApplicationUserEntity findByPanNumber(@Param("panNumber") String panNumber);

	@Transactional
	@Query(value = "SELECT u FROM tbl_application_master u WHERE TIMESTAMPDIFF(MINUTE, u.createdOn, CURRENT_TIMESTAMP) < 5 and email_verified=1 and sms_verified=1")
	List<ApplicationUserEntity> findRecentlyCreatedUsers();

	@Transactional
	@Query(value = "SELECT u FROM tbl_application_master u WHERE TIMESTAMPDIFF(MINUTE, u.createdOn, CURRENT_TIMESTAMP) < 5 and email_verified=1 and sms_verified=1 and stage=9")
	List<ApplicationUserEntity> findRecentlyCreatedUsersAll();

	@Modifying
	@Query(value = " UPDATE tbl_application_master SET status=:status, stage=:stage, esignCompleted=:esignCompleted, pdfGenerated =:pdfGenerated where id=:applicationId ")
	int updateEsignStage(@Param("applicationId") long applicationId, @Param("status") String status,
			@Param("stage") String stage, @Param("esignCompleted") int esignCompleted,
			@Param("pdfGenerated") int pdfGenerated);

	@Modifying
	@Query(value = " UPDATE tbl_application_master SET nomineeOptedOut=:nomineeOptedOut where id=:applicationId ")
	int updateNomineeOptedOut(@Param("applicationId") long applicationId,
			@Param("nomineeOptedOut") int nomineeOptedOut);

}

package in.codifi.api.repository;

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

	@Modifying
	@Query(value = " UPDATE tbl_application_master SET status=:status, stage=:stage, esignCompleted=:esignCompleted, pdfGenerated =:pdfGenerated, esigedName =:esigedName where id=:applicationId ")
	int updateEsignStage(@Param("applicationId") long applicationId, @Param("status") String status,
			@Param("stage") String stage, @Param("esignCompleted") int esignCompleted,
			@Param("pdfGenerated") int pdfGenerated, @Param("esigedName") String esigedName);

	@Modifying
	@Query(value = " UPDATE tbl_application_master SET nomineeOptedOut=:nomineeOptedOut where id=:applicationId ")
	int updateNomineeOptedOut(@Param("applicationId") long applicationId,
			@Param("nomineeOptedOut") int nomineeOptedOut);

	@Modifying
	@Query(value = " UPDATE tbl_application_master SET esignCompleted=:esignCompleted, pdfGenerated =:pdfGenerated, stage=:stage where id=:applicationId ")
	int updateRejectionStage(@Param("applicationId") long applicationId, @Param("esignCompleted") int esignCompleted,
			@Param("pdfGenerated") int pdfGenerated, @Param("stage") String stage);

	@Modifying
	@Query(value = " UPDATE tbl_application_master SET stage=:stage where id=:applicationId ")
	int updateIvrStage(@Param("applicationId") long applicationId, @Param("stage") String stage);

	@Transactional
	@Query(value = " SELECT max(id) FROM tbl_application_master ")
	Long findMaxValueOfReqId();

}

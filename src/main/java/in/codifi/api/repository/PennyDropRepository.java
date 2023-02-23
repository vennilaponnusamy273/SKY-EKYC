package in.codifi.api.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.PennyDropEntity;

public interface PennyDropRepository extends CrudRepository<PennyDropEntity, Long> {

	PennyDropEntity findByapplicationId(Long applicationId);

	@Transactional
	@Query(value = " SELECT A FROM tbl_penny_drop as A where A.applicationId = :applicationId and A.email = :email  and A.mobileNumber = :mobileNumber and A.pan = :pan")
	PennyDropEntity getPennyForContact(@Param("applicationId") Long applicationId, @Param("email") String email,
			@Param("mobileNumber") String mobileNumber, @Param("pan") String pan);

	@Transactional
	@Query(value = " SELECT A FROM tbl_penny_drop as A where A.applicationId = :applicationId and A.email = :email  and A.mobileNumber = :mobileNumber and A.pan = :pan and A.accNumber = :accNumber and A.ifsc = :ifsc")
	PennyDropEntity getPennyForContact(@Param("applicationId") Long applicationId, @Param("email") String email,
			@Param("mobileNumber") String mobileNumber, @Param("pan") String pan, @Param("accNumber") String accNumber,
			@Param("ifsc") String ifsc);
}

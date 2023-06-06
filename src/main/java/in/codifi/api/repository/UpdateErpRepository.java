package in.codifi.api.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import in.codifi.api.entity.UpdateErpEntity;

public interface UpdateErpRepository extends CrudRepository<UpdateErpEntity, Long> {

	UpdateErpEntity findByMobileNo(@Param("mobileNo") Long mobileNumber);
	UpdateErpEntity findByUserId(@Param("userid") String userid);
	 
}

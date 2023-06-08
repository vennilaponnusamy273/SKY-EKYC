package in.codifi.api.repository;
import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.UpdateErpEntity;

public interface UpdateErpRepository extends CrudRepository<UpdateErpEntity, Long> {

	UpdateErpEntity findByMobileNo(Long mobileNo);
	UpdateErpEntity findByUserIdAndDoctype(String  userId,String doctype); 
}

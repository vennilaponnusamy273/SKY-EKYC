package in.codifi.api.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.ServicesEntity;

public interface ServicesAccessRepository extends CrudRepository<ServicesEntity, Long> {

	@Transactional
	@Query(value = "SELECT access FROM tbl_services_rules where service = :service and order = :order")
	String getaccess(@Param("service") String service, @Param("order") String order);
}

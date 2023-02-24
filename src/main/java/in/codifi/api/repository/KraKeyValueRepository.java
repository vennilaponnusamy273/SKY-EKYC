package in.codifi.api.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.KraKeyValueEntity;

public interface KraKeyValueRepository extends CrudRepository<KraKeyValueEntity, Long> {

	@Transactional
	@Query(value = " SELECT kraValue FROM tbl_kra_keyvalue_pair as A where A.masterId = :masterId and A.masterName = :masterName  and A.kraKey = :kraKey")
	String getkeyValueForKra(@Param("masterId") String masterId, @Param("masterName") String masterName,
			@Param("kraKey") String kraKey);
}

package in.codifi.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.KraKeyValueEntity;

public interface KraKeyValueRepository extends CrudRepository<KraKeyValueEntity, Long> {

	@Transactional
	@Query(value = " SELECT kraValue FROM tbl_kra_keyvalue_pair where masterId = :masterId and masterName = :masterName  and kraKey = :kraKey ")
	String getkeyValueForKra(@Param("masterId") String masterId, @Param("masterName") String masterName,
			@Param("kraKey") String kraKey);

	List<KraKeyValueEntity> findByMasterIdAndMasterName(String masterId, String masterName);

}

package in.codifi.api.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.TrackWizzEntity;

public interface TrackWizzRepository extends CrudRepository<TrackWizzEntity, Long> {

	TrackWizzEntity findByapplicationId(Long applicationId);

	@Transactional
	@Query(value = " SELECT max(trackwizzReqId) FROM tbl_track_wizz ")
	Long findMaxValueOfReqId();

}

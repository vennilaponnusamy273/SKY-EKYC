package in.codifi.api.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.PdfDataCoordinatesEntity;

public interface PdfDataCoordinatesrepository extends CrudRepository<PdfDataCoordinatesEntity, Long> {

	/**
	 * Method to get pdf coordinates data
	 * 
	 * @author gowthaman
	 * @return
	 */
	@Transactional
	@Query(value = " SELECT A FROM tbl_pdf_data_coordinates  A WHERE A.activeStatus = 1")
	List<PdfDataCoordinatesEntity> getCoordinates();

	@Transactional
	@Query(value = "SELECT e.xCoordinate, e.yCoordinate, e.pageNo FROM tbl_pdf_data_coordinates e WHERE e.columnNames = 'esign' and e.activeStatus = 1")
	List<PdfDataCoordinatesEntity> getEsignCoordinates();

	@Transactional
	@Query(value = "SELECT e.xCoordinate, e.yCoordinate, e.pageNo FROM tbl_pdf_data_coordinates e WHERE e.columnNames = 'esign' and e.activeStatus = 1")
	List<Object[]> getEsignCoordinates1();

	List<PdfDataCoordinatesEntity> findByColumnNamesAndActiveStatus(String columnNames, int activeStatus);

	List<PdfDataCoordinatesEntity> findByColumnNamesAndActiveStatusAndPageNo(String string, int i, String string2);

}

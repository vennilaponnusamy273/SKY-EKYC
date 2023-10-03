package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_pdf_data_coordinates")
public class PdfDataCoordinatesEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "column_names")
	private String columnNames;

	@Column(name = "column_type")
	private String columnType;

	@Column(name = "x_coordinate")
	private String xCoordinate;

	@Column(name = "y_coordinate")
	private String yCoordinate;

	@Column(name = "page_no")
	private String pageNo;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "active_status")
	private int activeStatus = 1;

}
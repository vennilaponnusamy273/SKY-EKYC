package in.codifi.api.model;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NomineeDocModel {

	@RestForm("nomFile")
	private FileUpload nomFile;

	@RestForm("guardFile")
	private FileUpload guardFile;

	@FormParam(value = "applicationId")
	private long applicationId;

	@FormParam(value = "nomineeDetails")
	private String nomineeDetails;

}
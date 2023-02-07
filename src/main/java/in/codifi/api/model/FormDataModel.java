package in.codifi.api.model;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormDataModel {

	@RestForm("file")
	private FileUpload file;

	@FormParam(value = "applicationId")
	private long applicationId;

	@FormParam(value = "typeOfProof")
	private String typeOfProof;

	@FormParam(value = "password")
	private String password;
}

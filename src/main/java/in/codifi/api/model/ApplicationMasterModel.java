package in.codifi.api.model;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.ProfileEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApplicationMasterModel {

	private int applicationId;
	private ApplicationUserEntity applicationMasterEntity;
	private ProfileEntity profileEntity;
	private BankEntity bankEntity;
	private int stage;

}

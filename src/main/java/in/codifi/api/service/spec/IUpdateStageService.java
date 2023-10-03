package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface IUpdateStageService {

	public ResponseModel updateUser(long applicationId, String stage,String status);
		
}

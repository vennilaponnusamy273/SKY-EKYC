package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface IErpService {

	ResponseModel getuser(long mobile_no, String user_id,String EmailID,String Password);

	ResponseModel uploadDocument(String documentType, String userId, String base64Content);
}

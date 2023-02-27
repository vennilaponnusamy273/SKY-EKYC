package in.codifi.api.service.spec;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.model.ResponseModel;

public interface ICkycService {

	
	/**
	 * Method to save address Details
	 * 
	 * @author prade
	 * @param ResponseCkyc
	 * @return
	 */
	
	ResponseModel ckyc(ResponseCkyc ckyc);
}

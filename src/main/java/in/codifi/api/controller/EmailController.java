package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IEmailController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.utilities.CommonMethods;

@Path("/email")
public class EmailController implements IEmailController {

	@Inject
	CommonMethods commonMethods;

	/**
	 * test Method
	 */
	public ResponseModel test() {
		commonMethods.test();
		commonMethods.sendOTPtoMobile(1234, 8526707787L);
		ResponseModel model = new ResponseModel();
		model.setMessage("test");
		return model;
	}

}

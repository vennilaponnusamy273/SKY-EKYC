package in.codifi.api.controller;

import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IEmailController;
import in.codifi.api.model.ResponseModel;

@Path("/email")
public class EmailController implements IEmailController {


	/**
	 * test Method
	 */
	public ResponseModel test() {
		ResponseModel model = new ResponseModel();
		model.setMessage("test");
		return model;
	}
	
}

package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IvrModel {

	private String imageUrl;
	private Long applicationId;
	private String latitude;
	private String longitude;
	private String otp;
	private boolean isMobile;

}

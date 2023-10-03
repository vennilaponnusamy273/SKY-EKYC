package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NomineeAllocationModel {

	private Long applicationId;
	private int nomOneAllocation;
	private int nomTwoAllocation;
	private int nomThreeAllocation;

}

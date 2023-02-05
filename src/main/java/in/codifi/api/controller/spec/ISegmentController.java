package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.model.ResponseModel;

public interface ISegmentController {
	/**
	 * Method to save Segment Details
	 * 
	 * @author prade
	 * @param segmentEntity
	 * @return
	 */
	@Path("/saveSegment")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to save Segment Details")
	ResponseModel saveSegment(SegmentEntity segmentEntity);

	/**
	 * Method to get Segment Details
	 * 
	 * @author prade
	 * @param applicationId
	 * @return
	 */
	@Path("/getSegment")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get Segment Details")
	ResponseModel getSegmentByAppId(@NotNull @QueryParam("applicationId") long applicationId);
}

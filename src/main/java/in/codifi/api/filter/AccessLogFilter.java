package in.codifi.api.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.AccesslogEntity;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;
import io.quarkus.arc.Priority;
import io.vertx.core.http.HttpServerRequest;

@Provider
@Priority(Priorities.USER)
public class AccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {
	@Inject
	HttpServerRequest serverRequest;
	@Inject
	AccesslogRepository logRepository;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to capture and single save request and response
	 * 
	 * @param requestContext
	 * @param responseContext
	 */
	@SuppressWarnings("unused")
	private void caputureInSingleShot(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		String deviceIp = serverRequest.remoteAddress().toString();
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectMapper objMapper = new ObjectMapper();
					AccesslogEntity logModel = new AccesslogEntity();
					UriInfo uriInfo = requestContext.getUriInfo();
					MultivaluedMap<String, String> headers = requestContext.getHeaders();
					logModel.setContentType(headers.getFirst(EkycConstants.CONSTANT_CONTENT_TYPE));
					logModel.setDeviceIp(deviceIp);
					logModel.setMethod(requestContext.getMethod());
					String queryParams = uriInfo.getRequestUri().getQuery();
					if (StringUtil.isNotNullOrEmpty(queryParams)) {
						logModel.setReqBody(queryParams);
					} else {
						logModel.setReqBody(objMapper.writeValueAsString(requestContext.getProperty("reqBody")));
					}
					Object reponseObj = responseContext.getEntity();
					logModel.setResBody(objMapper.writeValueAsString(reponseObj));
					logModel.setUri(uriInfo.getPath().toString());
					logModel.setUserAgent(headers.getFirst(EkycConstants.USER_AGENT));
					logModel.setApplicationId(
							objMapper.writeValueAsString(requestContext.getProperty("applicationId")));
					logModel.setReqId(requestContext.getProperty("threadId") != null
							? requestContext.getProperty("threadId").toString()
							: "singlecapture");
					Long thredId = Thread.currentThread().getId();
					logRepository.save(logModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();

	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		caputureInSingleShot(requestContext, responseContext);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			requestContext.setProperty("inTime", new Timestamp(System.currentTimeMillis()));
			byte[] body = requestContext.getEntityStream().readAllBytes();
			InputStream stream = new ByteArrayInputStream(body);
			requestContext.setEntityStream(stream);
			String formedReq = new String(body);
			requestContext.setProperty("reqBody", formedReq);
//			String authorizationHeader = null;
//			String path = requestContext.getUriInfo().getPath();
//			if (!path.endsWith("user/sendSmsOtp")) {
//				authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
//				if (StringUtil.isNullOrEmpty(authorizationHeader)) {
//					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
//				} else {
//					validateToken(requestContext, authorizationHeader);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to validate the authorization token
	 * 
	 * @author prade
	 * 
	 * @param requestContext
	 * @param authorizationHeader
	 */
	public void validateToken(ContainerRequestContext requestContext, String authorizationHeader) {
		String fullToken = authorizationHeader.substring("Bearer".length()).trim();
		String token[] = fullToken.split(" ");
		if (StringUtil.isNotNullOrEmpty(token[0]) && StringUtil.isNotNullOrEmpty(token[1])) {
			String mobileNumber = commonMethods.decrypt(token[1]);
			if (HazleCacheController.getInstance().getAuthToken().containsKey(mobileNumber) && StringUtil
					.isEqual(HazleCacheController.getInstance().getAuthToken().get(mobileNumber), fullToken)) {
				HazleCacheController.getInstance().getAuthToken().put(mobileNumber, fullToken, 3600, TimeUnit.SECONDS);
			} else {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} else {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}
}

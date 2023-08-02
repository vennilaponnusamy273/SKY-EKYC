package in.codifi.api.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
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
	private static final List<String> disablepaths = Arrays.asList(EkycConstants.PATH_SEND_SMS_OTP,
			EkycConstants.PATH_TEST, EkycConstants.PATH_VERIFY_SMS_OTP,
			// EkycConstants.PATH_DIGI_WH,
			EkycConstants.PATH_RELOAD_KRAKEYVALUE, EkycConstants.PATH_GET_NSDL_ESIGN);

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
						logModel.setReqBody(
								objMapper.writeValueAsString(requestContext.getProperty(EkycConstants.CONST_REQ_BODY)));
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
			requestContext.setProperty(EkycConstants.CONST_IN_TIME, new Timestamp(System.currentTimeMillis()));
			byte[] body = requestContext.getEntityStream().readAllBytes();
			InputStream stream = new ByteArrayInputStream(body);
			requestContext.setEntityStream(stream);
			String formedReq = new String(body);
			requestContext.setProperty(EkycConstants.CONST_REQ_BODY, formedReq);
			String path = requestContext.getUriInfo().getPath();
			boolean visible = disablepaths.stream().anyMatch(visibleType -> visibleType.contains(path));
			if (!visible && StringUtil.isEqual(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.CONST_FILTER),
					EkycConstants.TRUE)) {
				String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
				if (StringUtil.isNullOrEmpty(authorizationHeader)) {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
				} else {
					validateToken(requestContext, authorizationHeader);
				}
			}
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
		if (StringUtil.isNotNullOrEmpty(fullToken)) {
			String token[] = fullToken.split(" ");
			if (StringUtil.isNotNullOrEmpty(token[0]) && StringUtil.isNotNullOrEmpty(token[1])) {
				String mobileNumber = commonMethods.decrypt(token[1]);
				if (StringUtil.isNotNullOrEmpty(mobileNumber)
						&& HazleCacheController.getInstance().getAuthToken().containsKey(mobileNumber)
						&& StringUtil.isEqual(HazleCacheController.getInstance().getAuthToken().get(mobileNumber),
								fullToken)) {
					UriInfo uriInfo = requestContext.getUriInfo();
					String queryParams = uriInfo.getRequestUri().getQuery();
					if (StringUtil.isNotNullOrEmpty(queryParams)) {
						long applciationId = getApplicationIdFromQueryParam(queryParams);
						String[] mobileAppId = mobileNumber.split("_");
						if (applciationId != Long.parseLong(mobileAppId[1])) {
							requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
						}
					}
					HazleCacheController.getInstance().getAuthToken().put(mobileNumber, fullToken, 300,
							TimeUnit.SECONDS);
				} else {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
				}
			} else {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} else {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	/**
	 * Method to get application Id from query param
	 * 
	 * @param queryParams
	 * @return
	 */
	private long getApplicationIdFromQueryParam(String queryParams) {
		long applicationID = 0l;
		String[] keyValue = queryParams.split("&");
		for (int i = 0; i < keyValue.length; i++) {
			String query = keyValue[i];
			if (query.contains("applicationId")) {
				String[] appIdValue = query.split("=");
				if (StringUtil.isNotNullOrEmpty(appIdValue[1]) && isNumeric(appIdValue[1])) {
					applicationID = Long.parseLong(appIdValue[1]);
				}
			}
		}
		return applicationID;
	}

	/**
	 * check string is muneric
	 * 
	 * @author prade
	 * @param strNum
	 * @return
	 */
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}

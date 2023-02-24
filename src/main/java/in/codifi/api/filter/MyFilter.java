package in.codifi.api.filter;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.entity.AccesslogEntity;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.utilities.StringUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Provider
@ApplicationScoped
public class MyFilter implements Handler<RoutingContext> {

	@Inject
	AccesslogRepository accessrepos;
	
	public String remoteaddress;
	public String uri;
	public String User_Agent;
	
	/**
	 * Filter Using RoutingContext Response
	 */
	@ServerRequestFilter
	@Override
	public void handle(RoutingContext event) {
		remoteaddress=event.request().remoteAddress().toString();
		uri=event.request().uri();
		User_Agent=event.request().getHeader("User-Agent");
		//event.next();
	}
	/**
	 * Accesslog details store in Db
	 */
	public void saveAccessRequestAndResposne(String req, String res, String method, long applicationId) {
		//@SuppressWarnings("unused")
		AccesslogEntity savedResult = null;
		if (StringUtil.isNotNullOrEmpty(req) && StringUtil.isNotNullOrEmpty(res) && StringUtil.isNotNullOrEmpty(method)
				&& applicationId > 0) {
			AccesslogEntity oldReqRes = accessrepos.findByApplicationIdAndType(applicationId, method);
			if (oldReqRes != null) {
				oldReqRes.setDevice_ip(remoteaddress);
				oldReqRes.setUri(uri);
				oldReqRes.setUser_agent(User_Agent);
				oldReqRes.setRequest_data(req);
				oldReqRes.setResponse_data(res);
				oldReqRes.setType(method);
				savedResult = accessrepos.save(oldReqRes);
			} else {
				AccesslogEntity savingResult = new AccesslogEntity();
				savingResult.setApplicationId(applicationId);
				savingResult.setDevice_ip(remoteaddress);
				savingResult.setUri(uri);
				savingResult.setUser_agent(User_Agent);
				savingResult.setRequest_data(req);
				savingResult.setResponse_data(res);
				savingResult.setType(method);
				savedResult =accessrepos.save(savingResult);
			}

		}

		
	}
	
	/**
	 * Accesslog Objects Convert to the String
	 */
	
	public void Access_Req_Res_Save_object(Object req, Object res, String method, long id) {
	{
		try {
		ObjectMapper mapper = new ObjectMapper();
		String Req = mapper.writeValueAsString(req);
		String Res;
		Res = mapper.writeValueAsString(res);
		System.out.println(req);
		saveAccessRequestAndResposne(Req,Res,method, id);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	}

	

}
	

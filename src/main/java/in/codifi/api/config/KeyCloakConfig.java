package in.codifi.api.config;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Singleton
@Getter
@Setter
public class KeyCloakConfig {

	@ConfigProperty(name = "quarkus.oidc.auth-server-url")
	String authServerUrl;

	@ConfigProperty(name = "quarkus.oidc.client-id")
	String clientId;

	@ConfigProperty(name = "quarkus.oidc.credentials.secret")
	String clientSecret;

	@ConfigProperty(name = "auth.org.grant.type")
	String grantType;

	@ConfigProperty(name = "auth.org.server.client-id")
	String adminClientId;

	@ConfigProperty(name = "auth.org.server.client-secret")
	String adminSecret;

	@ConfigProperty(name = "auth.org.server.grant-type")
	String adminGrantType;

	@ConfigProperty(name = "auth.org.server.url")
	String authOrgServerUrl;

	@ConfigProperty(name = "auth.org.server.realm")
	String authOrgServerRealM;

}

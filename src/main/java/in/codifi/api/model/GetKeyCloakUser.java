package in.codifi.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetKeyCloakUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private String id;
	@JsonProperty("createdTimestamp")
	private Long createdTimestamp;
	@JsonProperty("username")
	private String username;
	@JsonProperty("enabled")
	private Boolean enabled;
	@JsonProperty("totp")
	private Boolean totp;
	@JsonProperty("emailVerified")
	private Boolean emailVerified;
	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("email")
	private String email;
	@JsonProperty("disableableCredentialTypes")
	private List<Object> disableableCredentialTypes;
	@JsonProperty("requiredActions")
	private List<Object> requiredActions;
	@JsonProperty("notBefore")
	private Integer notBefore;
	@JsonProperty("access")
	private Access access;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("createdTimestamp")
	public Long getCreatedTimestamp() {
		return createdTimestamp;
	}

	@JsonProperty("createdTimestamp")
	public void setCreatedTimestamp(Long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty("enabled")
	public Boolean getEnabled() {
		return enabled;
	}

	@JsonProperty("enabled")
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@JsonProperty("totp")
	public Boolean getTotp() {
		return totp;
	}

	@JsonProperty("totp")
	public void setTotp(Boolean totp) {
		this.totp = totp;
	}

	@JsonProperty("emailVerified")
	public Boolean getEmailVerified() {
		return emailVerified;
	}

	@JsonProperty("emailVerified")
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@JsonProperty("firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@JsonProperty("lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("disableableCredentialTypes")
	public List<Object> getDisableableCredentialTypes() {
		return disableableCredentialTypes;
	}

	@JsonProperty("disableableCredentialTypes")
	public void setDisableableCredentialTypes(List<Object> disableableCredentialTypes) {
		this.disableableCredentialTypes = disableableCredentialTypes;
	}

	@JsonProperty("requiredActions")
	public List<Object> getRequiredActions() {
		return requiredActions;
	}

	@JsonProperty("requiredActions")
	public void setRequiredActions(List<Object> requiredActions) {
		this.requiredActions = requiredActions;
	}

	@JsonProperty("notBefore")
	public Integer getNotBefore() {
		return notBefore;
	}

	@JsonProperty("notBefore")
	public void setNotBefore(Integer notBefore) {
		this.notBefore = notBefore;
	}

	@JsonProperty("access")
	public Access getAccess() {
		return access;
	}

	@JsonProperty("access")
	public void setAccess(Access access) {
		this.access = access;
	}

}

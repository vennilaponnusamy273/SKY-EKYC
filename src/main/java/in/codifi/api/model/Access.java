package in.codifi.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Access implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("manageGroupMembership")
	private Boolean manageGroupMembership;
	@JsonProperty("view")
	private Boolean view;
	@JsonProperty("mapRoles")
	private Boolean mapRoles;
	@JsonProperty("impersonate")
	private Boolean impersonate;
	@JsonProperty("manage")
	private Boolean manage;

	@JsonProperty("manageGroupMembership")
	public Boolean getManageGroupMembership() {
		return manageGroupMembership;
	}

	@JsonProperty("manageGroupMembership")
	public void setManageGroupMembership(Boolean manageGroupMembership) {
		this.manageGroupMembership = manageGroupMembership;
	}

	@JsonProperty("view")
	public Boolean getView() {
		return view;
	}

	@JsonProperty("view")
	public void setView(Boolean view) {
		this.view = view;
	}

	@JsonProperty("mapRoles")
	public Boolean getMapRoles() {
		return mapRoles;
	}

	@JsonProperty("mapRoles")
	public void setMapRoles(Boolean mapRoles) {
		this.mapRoles = mapRoles;
	}

	@JsonProperty("impersonate")
	public Boolean getImpersonate() {
		return impersonate;
	}

	@JsonProperty("impersonate")
	public void setImpersonate(Boolean impersonate) {
		this.impersonate = impersonate;
	}

	@JsonProperty("manage")
	public Boolean getManage() {
		return manage;
	}

	@JsonProperty("manage")
	public void setManage(Boolean manage) {
		this.manage = manage;
	}
}

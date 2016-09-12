package org.nkjmlab.webui.common.user.model;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.nkjmlab.util.db.RelatedWithTable;
import org.nkjmlab.util.security.Hash;

import net.sf.persist.annotations.Table;

@Table(name = "USER_ACCOUNTS")
public class UserAccount implements RelatedWithTable {

	/** e-mail address, as a general rule. **/
	private Date created = new Timestamp(new Date().getTime());
	private Date modified = created;
	private String userId;
	private String groupId;
	private String name;
	private String password;
	private String role;
	private String options;
	private String mailAddress;
	private String language;

	public UserAccount() {
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public boolean validate(String password) {
		return this.password.equals(Hash.hash(getSalt(), password));
	}

	private String getSalt() {
		return String.valueOf(created.getTime());
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void setPasswordWithoutSalt(String sha1HashedButWithoutSaltPassword) {
		this.password = Hash.hash(getSalt(), password);
	}

}

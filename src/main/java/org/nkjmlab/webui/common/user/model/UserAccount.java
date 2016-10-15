package org.nkjmlab.webui.common.user.model;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.nkjmlab.util.bean.BeanUtils;
import org.nkjmlab.util.db.RelatedWithTable;

import net.sf.persist.annotations.Table;

@Table(name = "USER_ACCOUNTS")
public class UserAccount implements RelatedWithTable {

	/** e-mail address, as a general rule. **/
	private Date createdAt;
	private Date modifiedAt;
	private String userId;
	private String groupId;
	private String nickname;
	private String firstName;
	private String lastName;
	private String password;
	private String role;
	private String options;
	private String mailAddress;
	private String language;

	public enum Role {
		ADMIN(9), DEVELOPER(7), OPERATOR(5), RESTRICTED_OPERATOR(4), USER(3);

		private int level;

		Role(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	public UserAccount() {
	}

	public UserAccount(String userId, String groupId, String password, String langage,
			String nickname, Role role) {
		this.userId = userId;
		this.groupId = groupId;
		this.password = password;
		this.language = langage;
		this.nickname = nickname;
		this.role = role.name();
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date created) {
		this.createdAt = created;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String name) {
		this.nickname = name;
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
		return this.password
				.equals(DigestUtils.sha256Hex(password + getSalt()));
	}

	private String getSalt() {
		return String.valueOf(createdAt.getTime());
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modified) {
		this.modifiedAt = modified;
	}

	public void setPasswordWithoutSalt(String sha256HashedButWithoutSaltPassword) {
		this.password = DigestUtils.sha256Hex(sha256HashedButWithoutSaltPassword + getSalt());
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isAdmin() {
		return getRole().equals(Role.ADMIN.name());
	}

	public boolean isSameUserAccount(UserAccount account) {
		if (userId.equals(account.userId) && groupId.equals(account.groupId)) {
			return true;
		}
		return false;
	}

	public void mergeWith(UserAccount src) {
		BeanUtils.mergeWith(this, src);
	}

}

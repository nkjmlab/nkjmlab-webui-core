package org.nkjmlab.webui.service.user.model;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.nkjmlab.util.bean.BeanUtils;
import org.nkjmlab.util.db.RelatedWithTable;

import net.sf.persist.annotations.NoColumn;
import net.sf.persist.annotations.Table;

@Table(name = UserAccountsTable.TABLE_NAME)
public class UserAccount implements RelatedWithTable {

	private static final String SALT = "801iljmlkgfa796y19holj1ljl";

	private Date createdAt;
	private Date modifiedAt;
	private String userId;
	private String groupName;
	private String nickname;
	private String firstName;
	private String lastName;
	private String password;
	private String role;
	private String mail;
	private String language;
	private String options;

	private String encryptedInputPassword;

	@NoColumn
	public String getEncryptedInputPassword() {
		return encryptedInputPassword;
	}

	@NoColumn
	public void setEncryptedInputPassword(String encryptedInputPassword) {
		this.encryptedInputPassword = encryptedInputPassword;
		this.password = DigestUtils.sha256Hex(encryptedInputPassword + getSalt());
	}

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

	public UserAccount(String userId, String groupName, String encrypedInputPassword,
			String langage, String nickname, Role role) {
		this.userId = userId;
		setGroupName(groupName);
		setLanguage(langage);
		setNickname(nickname);
		setEncryptedInputPassword(encrypedInputPassword);
		this.role = role == null ? Role.USER.name() : role.name();
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mailAddress) {
		this.mail = mailAddress;
	}

	public boolean validate(String password) {
		return this.password.equals(DigestUtils.sha256Hex(password + getSalt()));
	}

	private String getSalt() {
		return SALT;
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
		return getRoleObject().getLevel() >= Role.ADMIN.getLevel();
	}

	private Role getRoleObject() {
		return Role.valueOf(getRole());
	}

	public boolean isOperator() {
		return getRoleObject().getLevel() >= Role.RESTRICTED_OPERATOR.getLevel();
	}

	public boolean isSameUserAccount(UserAccount account) {
		if (userId.equals(account.getUserId())) {
			return true;
		}
		return false;
	}

	public void setIfAbsent(UserAccount src) {
		BeanUtils.setIfAbsent(this, src);
	}

	@NoColumn
	public Locale getLocale() {
		try {
			return Locale.forLanguageTag(language);
		} catch (Throwable t) {
			return Locale.US;
		}
	}

}

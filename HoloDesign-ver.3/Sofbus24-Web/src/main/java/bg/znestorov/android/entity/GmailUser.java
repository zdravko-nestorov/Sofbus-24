package bg.znestorov.android.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import bg.znestorov.android.pojo.AppRole;

public class GmailUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String gmailId;
	private final String email;
	private final String nickname;
	private final Set<AppRole> authorities;

	private static final String EMAIL_ZDRAVKO = "zdravko.nestorov@gmail.com";

	public GmailUser(String gmailId, String email, String nickname,
			Set<AppRole> authorities) {
		super();
		this.gmailId = gmailId;
		this.email = email;
		this.nickname = nickname;
		this.authorities = authorities;
	}

	public GmailUser(String gmailId, String email, String nickname) {
		super();
		this.gmailId = gmailId;
		this.email = email;
		this.nickname = nickname;
		this.authorities = getRoleViaEmail(email);
	}

	public String getGmailId() {
		return gmailId;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}

	public Set<AppRole> getAuthorities() {
		return authorities;
	}

	/**
	 * Assign the user the appropriate role according to the email address (only
	 * my email addresses will be ADMINS)
	 * 
	 * @param email
	 *            the email address
	 * @return the roles of the user
	 */
	private Set<AppRole> getRoleViaEmail(String email) {

		Set<AppRole> roles = new HashSet<AppRole>();
		if (EMAIL_ZDRAVKO.equals(email)) {
			roles.add(AppRole.ADMIN);
		} else {
			roles.add(AppRole.USER);
		}

		return roles;
	}

	@Override
	public String toString() {
		return "GmailUser [gmailId=" + gmailId + ", email=" + email
				+ ", nickname=" + nickname + ", authorities=" + authorities
				+ "]";
	}

}
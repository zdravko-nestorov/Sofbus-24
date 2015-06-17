package bg.znestorov.android.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import bg.znestorov.android.pojo.AppRole;
import bg.znestorov.android.utils.Utils;

public class GmailUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String gmailId;
	private final String email;
	private final String nickname;
	private final Set<AppRole> authorities;
	private final String registrationDate;
	private final String lastOnlineDate;

	private static final String EMAIL_ZANIO_89 = "zanio89@gmail.com";
	private static final String EMAIL_ZDRAVKO_NESTOROV = "zdravko.nestorov@gmail.com";

	public GmailUser(String gmailId, String email, String nickname,
			Set<AppRole> authorities, String registrationDate,
			String lastOnlineDate) {
		super();
		this.gmailId = gmailId;
		this.email = email;
		this.nickname = nickname;
		this.authorities = authorities;
		this.registrationDate = registrationDate;
		this.lastOnlineDate = lastOnlineDate;
	}

	public GmailUser(String gmailId, String email, String nickname) {
		super();
		this.gmailId = gmailId;
		this.email = email;
		this.nickname = nickname;
		this.authorities = getRoleViaEmail(email);
		this.registrationDate = Utils.getCurrentDateTime();
		this.lastOnlineDate = Utils.getCurrentDateTime();
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

	public String getRegistrationDate() {
		return registrationDate;
	}

	public String getLastOnlineDate() {
		return lastOnlineDate;
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
		if (EMAIL_ZANIO_89.equals(email)
				|| EMAIL_ZDRAVKO_NESTOROV.equals(email)) {
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
				+ ", registrationDate=" + registrationDate
				+ ", lastOnlineDate=" + lastOnlineDate + "]";
	}

}
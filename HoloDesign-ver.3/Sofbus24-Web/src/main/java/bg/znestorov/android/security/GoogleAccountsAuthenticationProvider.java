package bg.znestorov.android.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import bg.znestorov.android.entity.GmailUser;
import bg.znestorov.android.jpa.GmailUserRegistry;
import bg.znestorov.android.pojo.GaeUserAuthentication;

import com.google.appengine.api.users.User;

public class GoogleAccountsAuthenticationProvider implements
		AuthenticationProvider {

	@Autowired
	private GmailUserRegistry userRegistry;

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		User googleUser = (User) authentication.getPrincipal();
		GmailUser user = userRegistry.findGmailUser(googleUser.getUserId());

		// User not in registry. Needs to register
		if (user == null) {
			user = new GmailUser(googleUser.getUserId(), googleUser.getEmail(),
					googleUser.getNickname());
			userRegistry.registerGmailUser(user);
		} else {
			userRegistry.updateGmailUser(user);
		}

		return new GaeUserAuthentication(user, authentication.getDetails());
	}

	public final boolean supports(Class<?> authentication) {
		return PreAuthenticatedAuthenticationToken.class
				.isAssignableFrom(authentication);
	}

	public void setUserRegistry(GmailUserRegistry userRegistry) {
		this.userRegistry = userRegistry;
	}
}
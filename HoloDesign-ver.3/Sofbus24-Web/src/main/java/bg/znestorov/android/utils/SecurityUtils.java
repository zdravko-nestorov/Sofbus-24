package bg.znestorov.android.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import bg.znestorov.android.entity.GmailUser;
import bg.znestorov.android.jpa.GmailUserRegistry;

public class SecurityUtils {

	/**
	 * Get the current logged in user
	 * 
	 * @param userRegistry
	 *            the user registry (autowired by Spring)
	 * @return the gmail user
	 */
	public static GmailUser getCurrentUser(GmailUserRegistry userRegistry) {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		GmailUser user = (GmailUser) authentication.getPrincipal();

		return user;
	}

}
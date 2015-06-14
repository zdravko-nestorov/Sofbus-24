package bg.znestorov.android.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import bg.znestorov.android.pojo.AppRole;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("rawtypes")
public class GaeAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private AuthenticationManager authenticationManager;

	private AuthenticationDetailsSource аuthenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		// User isn't authenticated. Check if there is a Google Accounts user
		if (authentication == null) {
			User googleUser = UserServiceFactory.getUserService()
					.getCurrentUser();

			// User has returned after authenticating through GAE. Need to
			// authenticate to Spring Security.
			if (googleUser != null) {
				PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
						googleUser, null);
				token.setDetails(аuthenticationDetailsSource
						.buildDetails(request));

				try {
					authentication = authenticationManager.authenticate(token);

					// Setup the security context
					SecurityContextHolder.getContext().setAuthentication(
							authentication);

					if (authentication.getAuthorities().contains(AppRole.USER)) {
						((HttpServletResponse) response)
								.sendRedirect("/access-denied");
						return;
					}
				} catch (AuthenticationException e) {
					// Authentication information was rejected by the
					// authentication manager
					failureHandler.onAuthenticationFailure(
							(HttpServletRequest) request,
							(HttpServletResponse) response, e);
					return;
				}
			}
		}

		chain.doFilter(request, response);
	}

	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
		this.failureHandler = failureHandler;
	}

}
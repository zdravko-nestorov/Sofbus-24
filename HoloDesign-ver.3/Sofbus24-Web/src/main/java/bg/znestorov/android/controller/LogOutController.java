package bg.znestorov.android.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
public class LogOutController {

	@RequestMapping(value = "/log-out", method = RequestMethod.GET)
	public ModelAndView accessDenied(HttpServletRequest request,
			HttpServletResponse response) {

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		SecurityContextLogoutHandler ctxLogOut = new SecurityContextLogoutHandler();
		ctxLogOut.logout(request, response, auth);

		UserService userService = UserServiceFactory.getUserService();
		String logoutUrl = userService.createLogoutURL(userService
				.createLoginURL("/index"));

		ModelAndView modelView = new ModelAndView("redirect:" + logoutUrl);

		return modelView;
	}
}
package bg.znestorov.android.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

	/**
	 * Handle all mappings to an incorrect URL addresses
	 */
	@RequestMapping("/**")
	public ModelAndView error404() {

		ModelAndView modelView = new ModelAndView("errors/error404");

		return modelView;
	}

	@RequestMapping(value = "/access-denied", method = RequestMethod.GET)
	public ModelAndView accessDenied(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView modelView = new ModelAndView("errors/access-denied");

		return modelView;
	}

}
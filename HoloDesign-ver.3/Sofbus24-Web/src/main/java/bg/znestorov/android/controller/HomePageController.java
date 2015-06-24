package bg.znestorov.android.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import bg.znestorov.android.jpa.GmailUserRegistry;

@Controller
public class HomePageController {

	@Autowired
	private GmailUserRegistry userRegistry;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView accessDenied() {

		ModelAndView modelView = new ModelAndView("redirect:/index");

		return modelView;
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView registerContact() {

		ModelAndView modelView = new ModelAndView("index");
		modelView.addObject("gmailUsersList", userRegistry.findAllGmailUsers());

		return modelView;
	}

}
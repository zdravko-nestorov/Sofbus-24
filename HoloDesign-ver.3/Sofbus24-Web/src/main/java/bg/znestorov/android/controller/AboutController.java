package bg.znestorov.android.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AboutController {

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public ModelAndView registerContact() {

		ModelAndView modelView = new ModelAndView("about");

		return modelView;
	}
}
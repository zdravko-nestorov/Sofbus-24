package bg.znestorov.android.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import bg.znestorov.android.entity.GmailUser;
import bg.znestorov.android.jpa.GmailUserRegistry;
import bg.znestorov.android.utils.SecurityUtils;

@Controller
public class ContactUsController {

	@Autowired
	private GmailUserRegistry userRegistry;

	@Autowired
	private MailSender mailSender;

	private static final String ADMIN_EMAIL = "zdravko.nestorov@gmail.com";

	@RequestMapping(value = "/contact-us", method = RequestMethod.GET)
	public ModelAndView registerContact() {

		GmailUser user = SecurityUtils.getCurrentUser(userRegistry);

		ModelAndView modelView = new ModelAndView("contact-us");
		modelView.addObject("user", user);

		return modelView;
	}

	@RequestMapping(value = "/send-email", method = RequestMethod.POST)
	public ModelAndView sendMail(
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "subject", required = true) String subject,
			@RequestParam(value = "msg", required = true) String msg) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(email);
		message.setTo(ADMIN_EMAIL);
		message.setSubject(subject);
		message.setText(msg);
		mailSender.send(message);

		ModelAndView modelView = new ModelAndView("contact-us");

		return modelView;
	}
}
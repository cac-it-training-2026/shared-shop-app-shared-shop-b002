package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {

	@Autowired
	HttpSession session;
	@Autowired
	UserRepository repository;

	@RequestMapping(path = { "/client/user/regist/input/init", "/client/user/regist/input" })
	public String registInput(Model model) {
		UserForm form = (UserForm) session.getAttribute("userForm");
		if (form == null) {
			form = new UserForm();
			session.setAttribute("userForm", form);
		}
		model.addAttribute("userForm", form);
		return "client/user/regist_input";
	}

	@RequestMapping(path = "/client/user/regist/check", method = { RequestMethod.POST, RequestMethod.GET })
	public String registCheck(@Valid @ModelAttribute UserForm form, BindingResult result, Model model) {
		session.setAttribute("userForm", form);
		if (result.hasErrors()) {
			model.addAttribute("userForm", form);
			return "client/user/regist_input";
		}

		model.addAttribute("userForm", form);
		return "client/user/regist_check";
	}

	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String registComplete() {
		UserForm form = (UserForm) session.getAttribute("userForm");

		if (form != null) {
			User user = new User();
			user.setName(form.getName());
			repository.save(user);
			session.removeAttribute("userForm");
			session.setAttribute("user", user);
		}

		return "client/user/regist_complete";
	}
}

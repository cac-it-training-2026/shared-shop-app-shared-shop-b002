package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.form.UserForm;

@Controller
public class ClientUserUpdateController {

	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateUserCheck(@Valid @ModelAttribute UserForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {

			return "redirect:/client/user/update/input";

		}

		return "client/user/update_check";

	}

	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateUserComplete(@ModelAttribute UserForm form, HttpSession session) {

		return "client/user/update_complete";

	}

}
